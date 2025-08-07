package com.agroflow.ms_facturacion.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.agroflow.ms_facturacion.util.EstadoFactura;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "factura_id")
    private UUID facturaId;

    @Column(name = "cosecha_id", nullable = false, unique = true)
    private UUID cosechaId;

    @Column(name = "numero_factura", nullable = false, unique = true)
    private String numeroFactura;

    @Column(name = "producto", nullable = false)
    private String producto;

    @Column(name = "toneladas", nullable = false, precision = 10, scale = 2)
    private BigDecimal toneladas;

    @Column(name = "precio_por_tonelada", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioPorTonelada;

    @Column(name = "monto_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoFactura estado = EstadoFactura.PENDIENTE;

    @Column(name = "fecha_factura", nullable = false, updatable = false)
    private LocalDateTime fechaFactura = LocalDateTime.now();

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDateTime fechaVencimiento;

    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();
}