package com.agroflow.ms_central.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.agroflow.ms_central.config.RabbitConfig;
import com.agroflow.ms_central.dto.CosechaResponseDto;
import com.agroflow.ms_central.dto.CreateCosechaRequestDto;
import com.agroflow.ms_central.dto.NuevaCosechaEvent;
import com.agroflow.ms_central.entity.Agricultor;
import com.agroflow.ms_central.entity.Cosecha;
import com.agroflow.ms_central.repository.AgricultorRepository;
import com.agroflow.ms_central.repository.CosechaRepository;
import com.agroflow.ms_central.util.EstadoCosecha;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CosechaService {

    private final CosechaRepository cosechaRepository;
    private final AgricultorRepository agricultorRepository;
    private final RabbitTemplate rabbitTemplate;

    public CosechaResponseDto crearCosecha(CreateCosechaRequestDto request) {
        // Buscar agricultor
        Agricultor agricultor = agricultorRepository.findById(request.getAgricultorId())
                .orElse(null);
        
        if (agricultor == null) {
            log.error("Agricultor no encontrado con ID: {}", request.getAgricultorId());
            return null;
        }

        // Crear cosecha
        Cosecha cosecha = new Cosecha();
        cosecha.setAgricultor(agricultor);
        cosecha.setProducto(request.getProducto());
        cosecha.setToneladas(request.getToneladas());
        cosecha.setUbicacion(request.getUbicacion());
        cosecha.setEstado(EstadoCosecha.REGISTRADA);

        Cosecha cosechaGuardada = cosechaRepository.save(cosecha);
        log.info("Cosecha creada exitosamente: {}", cosechaGuardada.getCosechaId());

        // Publicar evento a RabbitMQ
        publicarEventoNuevaCosecha(cosechaGuardada);

        // Convertir a DTO
        return convertirAResponseDto(cosechaGuardada);
    }

    public List<CosechaResponseDto> obtenerTodasLasCosechas() {
        List<Cosecha> cosechas = cosechaRepository.findAll();
        return cosechas.stream()
                .map(this::convertirAResponseDto)
                .toList();
    }

    public CosechaResponseDto obtenerCosechaPorId(UUID cosechaId) {
        Cosecha cosecha = cosechaRepository.findByIdWithAgricultor(cosechaId);
        if (cosecha == null) {
            log.warn("Cosecha no encontrada con ID: {}", cosechaId);
            return null;
        }
        return convertirAResponseDto(cosecha);
    }

    public CosechaResponseDto actualizarEstadoCosecha(UUID cosechaId, EstadoCosecha estado, UUID facturaId) {
        Cosecha cosecha = cosechaRepository.findById(cosechaId).orElse(null);
        if (cosecha == null) {
            log.error("Cosecha no encontrada para actualizar estado: {}", cosechaId);
            return null;
        }

        cosecha.setEstado(estado);
        cosecha.setFechaActualizacion(LocalDateTime.now());
        
        if (facturaId != null) {
            cosecha.setFacturaId(facturaId);
        }

        Cosecha cosechaActualizada = cosechaRepository.save(cosecha);
        log.info("Estado de cosecha actualizado: {} -> {}", cosechaId, estado);

        return convertirAResponseDto(cosechaActualizada);
    }

    private void publicarEventoNuevaCosecha(Cosecha cosecha) {
        try {
            NuevaCosechaEvent evento = new NuevaCosechaEvent();
            evento.setEventId(UUID.randomUUID());
            evento.setEventType("nueva_cosecha");
            evento.setTimestamp(LocalDateTime.now());

            NuevaCosechaEvent.Payload payload = new NuevaCosechaEvent.Payload();
            payload.setCosechaId(cosecha.getCosechaId());
            payload.setProducto(cosecha.getProducto());
            payload.setToneladas(cosecha.getToneladas());
            payload.setRequiereInsumos(getInsumosPorProducto(cosecha.getProducto()));

            evento.setPayload(payload);

            // Publicar al exchange "cosechas" con routing key "nueva"
            rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_COSECHAS, 
                RabbitConfig.ROUTING_KEY_NUEVA, 
                evento
            );

            log.info("Evento nueva_cosecha publicado exitosamente para cosecha: {}", cosecha.getCosechaId());

        } catch (Exception e) {
            log.error("Error al publicar evento nueva_cosecha para cosecha: {}", cosecha.getCosechaId(), e);
            // No relanzamos la excepción para no afectar la transacción principal
        }
    }

    private List<String> getInsumosPorProducto(String producto) {
        return switch (producto) {
            case "Arroz Oro" -> Arrays.asList("Semilla Arroz L-23", "Fertilizante N-PK");
            case "Café Premium" -> Arrays.asList("Semilla Café Arábica", "Fertilizante Orgánico");
            case "Maíz Amarillo" -> Arrays.asList("Semilla Maíz Híbrido", "Fertilizante NPK-15");
            default -> Arrays.asList();
        };
    }

    private CosechaResponseDto convertirAResponseDto(Cosecha cosecha) {
        CosechaResponseDto dto = new CosechaResponseDto();
        dto.setCosechaId(cosecha.getCosechaId());
        dto.setAgricultorId(cosecha.getAgricultor().getAgricultorId());
        dto.setNombreAgricultor(cosecha.getAgricultor().getNombre() + " " + cosecha.getAgricultor().getApellidos());
        dto.setProducto(cosecha.getProducto());
        dto.setToneladas(cosecha.getToneladas());
        dto.setUbicacion(cosecha.getUbicacion());
        dto.setEstado(cosecha.getEstado());
        dto.setFacturaId(cosecha.getFacturaId());
        dto.setFechaCosecha(cosecha.getFechaCosecha());
        return dto;
    }
}