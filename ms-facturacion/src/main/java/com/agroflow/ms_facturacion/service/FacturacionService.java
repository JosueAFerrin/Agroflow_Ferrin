package com.agroflow.ms_facturacion.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agroflow.ms_facturacion.client.CentralClient;
import com.agroflow.ms_facturacion.dto.FacturaResponseDto;
import com.agroflow.ms_facturacion.dto.NuevaCosechaEvent;
import com.agroflow.ms_facturacion.entity.Factura;
import com.agroflow.ms_facturacion.repository.FacturaRepository;
import com.agroflow.ms_facturacion.util.EstadoFactura;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FacturacionService {

    private final FacturaRepository facturaRepository;
    private final CentralClient centralClient;

    // Precios por tonelada según el producto
    private static final Map<String, BigDecimal> PRECIOS_POR_PRODUCTO = Map.of(
        "Arroz Oro", new BigDecimal("120.00"),
        "Café Premium", new BigDecimal("300.00"),
        "Maíz Amarillo", new BigDecimal("85.00")
    );

    @RabbitListener(queues = "cola-facturacion")
    @Transactional
    public void procesarNuevaCosecha(NuevaCosechaEvent evento) {
        log.info("Procesando facturación para cosecha: {}", evento.getPayload().getCosechaId());
        
        try {
            // Verificar si ya existe factura para esta cosecha
            if (facturaRepository.findByCosechaId(evento.getPayload().getCosechaId()).isPresent()) {
                log.warn("Ya existe factura para la cosecha: {}", evento.getPayload().getCosechaId());
                return;
            }

            // Crear factura
            Factura factura = crearFactura(evento.getPayload());
            Factura facturaGuardada = facturaRepository.save(factura);

            log.info("Factura creada exitosamente: {} para cosecha: {}", 
                    facturaGuardada.getNumeroFactura(), evento.getPayload().getCosechaId());

            // Actualizar estado en ms-central usando el string correcto
            boolean actualizado = centralClient.actualizarEstadoCosecha(
                evento.getPayload().getCosechaId(), 
                "FACTURADA", // String que será convertido en el ms-central
                facturaGuardada.getFacturaId()
            );

            if (actualizado) {
                log.info("Estado de cosecha actualizado a FACTURADA en ms-central para: {}", 
                        evento.getPayload().getCosechaId());
            } else {
                log.error("Error actualizando estado de cosecha en ms-central para: {}", 
                         evento.getPayload().getCosechaId());
            }

        } catch (Exception e) {
            log.error("Error procesando facturación para cosecha: {}", 
                     evento.getPayload().getCosechaId(), e);
        }
    }

    private Factura crearFactura(NuevaCosechaEvent.Payload payload) {
        BigDecimal precioPorTonelada = PRECIOS_POR_PRODUCTO.getOrDefault(
            payload.getProducto(), 
            new BigDecimal("100.00") // Precio por defecto
        );

        BigDecimal montoTotal = precioPorTonelada.multiply(payload.getToneladas());

        Factura factura = new Factura();
        factura.setCosechaId(payload.getCosechaId());
        factura.setNumeroFactura(generarNumeroFactura());
        factura.setProducto(payload.getProducto());
        factura.setToneladas(payload.getToneladas());
        factura.setPrecioPorTonelada(precioPorTonelada);
        factura.setMontoTotal(montoTotal);
        factura.setEstado(EstadoFactura.PENDIENTE);
        factura.setFechaFactura(LocalDateTime.now());
        factura.setFechaVencimiento(LocalDateTime.now().plusDays(30)); // 30 días para pagar
        factura.setObservaciones("Factura generada automáticamente por cosecha registrada");

        return factura;
    }

    private String generarNumeroFactura() {
        LocalDateTime now = LocalDateTime.now();
        String fecha = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // Contar facturas del día
        Long contador = facturaRepository.countFacturasPorFecha(now);
        String secuencial = String.format("%03d", contador + 1);
        
        return "F-" + fecha + "-" + secuencial;
    }

    public List<FacturaResponseDto> obtenerTodasLasFacturas() {
        List<Factura> facturas = facturaRepository.findAll();
        return facturas.stream()
                .map(this::convertirAResponseDto)
                .toList();
    }

    public FacturaResponseDto obtenerFacturaPorId(UUID facturaId) {
        Factura factura = facturaRepository.findById(facturaId).orElse(null);
        if (factura == null) {
            return null;
        }
        return convertirAResponseDto(factura);
    }

    public FacturaResponseDto obtenerFacturaPorCosecha(UUID cosechaId) {
        Factura factura = facturaRepository.findByCosechaId(cosechaId).orElse(null);
        if (factura == null) {
            return null;
        }
        return convertirAResponseDto(factura);
    }

    public List<FacturaResponseDto> obtenerFacturasPendientes() {
        List<Factura> facturas = facturaRepository.findByEstado(EstadoFactura.PENDIENTE);
        return facturas.stream()
                .map(this::convertirAResponseDto)
                .toList();
    }

    @Transactional
    public FacturaResponseDto marcarComoPagada(UUID facturaId) {
        Factura factura = facturaRepository.findById(facturaId).orElse(null);
        if (factura == null) {
            return null;
        }

        factura.setEstado(EstadoFactura.PAGADA);
        factura.setFechaPago(LocalDateTime.now());
        factura.setFechaActualizacion(LocalDateTime.now());

        Factura facturaActualizada = facturaRepository.save(factura);
        log.info("Factura marcada como PAGADA: {}", factura.getNumeroFactura());

        return convertirAResponseDto(facturaActualizada);
    }

    private FacturaResponseDto convertirAResponseDto(Factura factura) {
        FacturaResponseDto dto = new FacturaResponseDto();
        dto.setFacturaId(factura.getFacturaId());
        dto.setCosechaId(factura.getCosechaId());
        dto.setNumeroFactura(factura.getNumeroFactura());
        dto.setProducto(factura.getProducto());
        dto.setToneladas(factura.getToneladas());
        dto.setPrecioPorTonelada(factura.getPrecioPorTonelada());
        dto.setMontoTotal(factura.getMontoTotal());
        dto.setEstado(factura.getEstado());
        dto.setFechaFactura(factura.getFechaFactura());
        dto.setFechaVencimiento(factura.getFechaVencimiento());
        dto.setFechaPago(factura.getFechaPago());
        dto.setObservaciones(factura.getObservaciones());
        return dto;
    }
}