package com.agroflow.ms_central.dto;

import java.util.UUID;

import com.agroflow.ms_central.util.EstadoCosecha;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEstadoCosechaRequestDto {
    private EstadoCosecha estado;
    private UUID facturaId;
}