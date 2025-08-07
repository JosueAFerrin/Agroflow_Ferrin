package com.agroflow.ms_inventario.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agroflow.ms_inventario.dto.InsumoResponseDto;
import com.agroflow.ms_inventario.dto.InventarioAjustadoEvent;
import com.agroflow.ms_inventario.dto.NuevaCosechaEvent;
import com.agroflow.ms_inventario.entity.Insumo;
import com.agroflow.ms_inventario.entity.MovimientoInventario;
import com.agroflow.ms_inventario.repository.InsumoRepository;
import com.agroflow.ms_inventario.repository.MovimientoInventarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioService {

    private final InsumoRepository insumoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final RabbitTemplate rabbitTemplate;

    // Configuración de insumos requeridos por tonelada según el producto
    private static final Map<String, BigDecimal> INSUMOS_POR_TONELADA = Map.of(
        "Semilla Arroz L-23", new BigDecimal("5.0"),      // 5 kg por tonelada
        "Fertilizante N-PK", new BigDecimal("2.0"),       // 2 kg por tonelada
        "Semilla Café Arábica", new BigDecimal("3.0"),    // 3 kg por tonelada
        "Fertilizante Orgánico", new BigDecimal("4.0"),   // 4 kg por tonelada
        "Semilla Maíz Híbrido", new BigDecimal("6.0"),    // 6 kg por tonelada
        "Fertilizante NPK-15", new BigDecimal("2.5")      // 2.5 kg por tonelada
    );

    @RabbitListener(queues = "cola-inventario")
    @Transactional
    public void procesarNuevaCosecha(NuevaCosechaEvent evento) {
        log.info("Procesando evento nueva_cosecha para cosecha: {}", evento.getPayload().getCosechaId());
        
        try {
            boolean procesadoExitoso = true;
            StringBuilder mensaje = new StringBuilder();

            for (String nombreInsumo : evento.getPayload().getRequiereInsumos()) {
                boolean resultado = descontarInsumo(
                    nombreInsumo, 
                    evento.getPayload().getToneladas(), 
                    evento.getPayload().getCosechaId()
                );
                
                if (!resultado) {
                    procesadoExitoso = false;
                    mensaje.append("Insumo sin stock suficiente: ").append(nombreInsumo).append(". ");
                }
            }

            // Publicar evento de confirmación
            InventarioAjustadoEvent confirmacion = new InventarioAjustadoEvent();
            confirmacion.setCosechaId(evento.getPayload().getCosechaId());
            confirmacion.setStatus(procesadoExitoso ? "OK" : "ERROR");
            confirmacion.setMensaje(procesadoExitoso ? "Inventario ajustado correctamente" : mensaje.toString());

            rabbitTemplate.convertAndSend("inventario-ajustado", confirmacion);
            
            log.info("Evento inventario_ajustado enviado para cosecha: {} con status: {}", 
                    evento.getPayload().getCosechaId(), confirmacion.getStatus());

        } catch (Exception e) {
            log.error("Error procesando evento nueva_cosecha para cosecha: {}", 
                     evento.getPayload().getCosechaId(), e);
            
            // Enviar evento de error
            InventarioAjustadoEvent error = new InventarioAjustadoEvent();
            error.setCosechaId(evento.getPayload().getCosechaId());
            error.setStatus("ERROR");
            error.setMensaje("Error interno procesando inventario: " + e.getMessage());
            
            rabbitTemplate.convertAndSend("inventario-ajustado", error);
        }
    }

    private boolean descontarInsumo(String nombreInsumo, BigDecimal toneladas, UUID cosechaId) {
        Insumo insumo = insumoRepository.findByNombreInsumo(nombreInsumo).orElse(null);
        
        if (insumo == null) {
            log.warn("Insumo no encontrado: {}", nombreInsumo);
            return false;
        }

        BigDecimal cantidadPorTonelada = INSUMOS_POR_TONELADA.getOrDefault(nombreInsumo, BigDecimal.ZERO);
        BigDecimal cantidadRequerida = cantidadPorTonelada.multiply(toneladas);

        if (insumo.getStock().compareTo(cantidadRequerida) < 0) {
            log.warn("Stock insuficiente para {}: Disponible={}, Requerido={}", 
                    nombreInsumo, insumo.getStock(), cantidadRequerida);
            return false;
        }

        // Registrar movimiento
        MovimientoInventario movimiento = new MovimientoInventario();
        movimiento.setInsumo(insumo);
        movimiento.setCosechaId(cosechaId);
        movimiento.setTipoMovimiento("SALIDA");
        movimiento.setCantidad(cantidadRequerida);
        movimiento.setStockAnterior(insumo.getStock());
        
        // Actualizar stock
        BigDecimal nuevoStock = insumo.getStock().subtract(cantidadRequerida);
        insumo.setStock(nuevoStock);
        insumo.setFechaActualizacion(LocalDateTime.now());
        
        movimiento.setStockNuevo(nuevoStock);
        movimiento.setObservaciones("Descuento automático por cosecha: " + cosechaId);

        insumoRepository.save(insumo);
        movimientoRepository.save(movimiento);

        log.info("Stock descontado - Insumo: {}, Cantidad: {}, Stock nuevo: {}", 
                nombreInsumo, cantidadRequerida, nuevoStock);

        return true;
    }

    public List<InsumoResponseDto> obtenerTodosLosInsumos() {
        List<Insumo> insumos = insumoRepository.findByActivoTrue();
        return insumos.stream()
                .map(this::convertirAResponseDto)
                .toList();
    }

    public List<InsumoResponseDto> obtenerInsumosConStockBajo() {
        List<Insumo> insumos = insumoRepository.findInsumosConStockBajo();
        return insumos.stream()
                .map(this::convertirAResponseDto)
                .toList();
    }

    private InsumoResponseDto convertirAResponseDto(Insumo insumo) {
        InsumoResponseDto dto = new InsumoResponseDto();
        dto.setInsumoId(insumo.getInsumoId());
        dto.setNombreInsumo(insumo.getNombreInsumo());
        dto.setDescripcion(insumo.getDescripcion());
        dto.setStock(insumo.getStock());
        dto.setStockMinimo(insumo.getStockMinimo());
        dto.setUnidadMedida(insumo.getUnidadMedida());
        dto.setPrecioUnitario(insumo.getPrecioUnitario());
        dto.setActivo(insumo.getActivo());
        dto.setFechaCreacion(insumo.getFechaCreacion());
        dto.setStockBajo(insumo.getStock().compareTo(insumo.getStockMinimo()) <= 0);
        return dto;
    }
}