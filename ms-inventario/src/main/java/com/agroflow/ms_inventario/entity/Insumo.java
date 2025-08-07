package com.agroflow.ms_inventario.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "insumos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "insumo_id")
    private UUID insumoId;

    @Column(name = "nombre_insumo", nullable = false, unique = true)
    private String nombreInsumo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "stock", nullable = false, precision = 10, scale = 2)
    private BigDecimal stock;

    @Column(name = "stock_minimo", nullable = false, precision = 10, scale = 2)
    private BigDecimal stockMinimo;

    @Column(name = "unidad_medida", nullable = false)
    private String unidadMedida; // kg, litros, unidades, etc.

    @Column(name = "precio_unitario", precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();
}