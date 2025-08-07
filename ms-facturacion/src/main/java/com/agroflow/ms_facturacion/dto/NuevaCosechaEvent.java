package com.agroflow.ms_facturacion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NuevaCosechaEvent {
    private UUID eventId;
    private String eventType;
    private LocalDateTime timestamp;
    private Payload payload;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payload {
        private UUID cosechaId;
        private String producto;
        private BigDecimal toneladas;
        private List<String> requiereInsumos;
    }
}