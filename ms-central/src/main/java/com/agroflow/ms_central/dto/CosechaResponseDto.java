package com.agroflow.ms_central.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.agroflow.ms_central.util.EstadoCosecha;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CosechaResponseDto {
    private UUID cosechaId;
    private UUID agricultorId;
    private String nombreAgricultor;
    private String producto;
    private BigDecimal toneladas;
    private String ubicacion;
    private EstadoCosecha estado;
    private UUID facturaId;
    private LocalDateTime fechaCosecha;
}