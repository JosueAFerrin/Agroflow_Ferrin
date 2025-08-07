package com.agroflow.ms_inventario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InsumoResponseDto {
    private UUID insumoId;
    private String nombreInsumo;
    private String descripcion;
    private BigDecimal stock;
    private BigDecimal stockMinimo;
    private String unidadMedida;
    private BigDecimal precioUnitario;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private Boolean stockBajo;
}