package com.agroflow.ms_inventario.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioAjustadoEvent {
    private String evento = "inventario_ajustado";
    private UUID cosechaId;
    private String status;
    private String mensaje;
    private LocalDateTime timestamp = LocalDateTime.now();
}