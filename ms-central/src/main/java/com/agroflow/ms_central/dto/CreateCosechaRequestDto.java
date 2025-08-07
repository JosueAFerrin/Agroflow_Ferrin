package com.agroflow.ms_central.dto;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCosechaRequestDto {
    private UUID agricultorId;
    private String producto;
    private BigDecimal toneladas;
    private String ubicacion;
}