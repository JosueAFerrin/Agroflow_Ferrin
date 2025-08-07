package com.agroflow.ms_facturacion.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEstadoCosechaDto {
    private String estado; // Mantenemos como String para enviar al endpoint
    private UUID facturaId;
}