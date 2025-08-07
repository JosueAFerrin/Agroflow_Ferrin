package com.agroflow.ms_facturacion.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.agroflow.ms_facturacion.util.EstadoFactura;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaResponseDto {
    private UUID facturaId;
    private UUID cosechaId;
    private String numeroFactura;
    private String producto;
    private BigDecimal toneladas;
    private BigDecimal precioPorTonelada;
    private BigDecimal montoTotal;
    private EstadoFactura estado;
    private LocalDateTime fechaFactura;
    private LocalDateTime fechaVencimiento;
    private LocalDateTime fechaPago;
    private String observaciones;
}