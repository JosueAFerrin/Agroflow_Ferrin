package com.agroflow.ms_central.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.agroflow.ms_central.util.EstadoCosecha;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cosechas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cosecha {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "cosecha_id")
    private UUID cosechaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agricultor_id", nullable = false)
    private Agricultor agricultor;

    @Column(name = "producto", nullable = false)
    private String producto;

    @Column(name = "toneladas", nullable = false, precision = 10, scale = 2)
    private BigDecimal toneladas;

    @Column(name = "ubicacion")
    private String ubicacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoCosecha estado = EstadoCosecha.REGISTRADA;

    @Column(name = "factura_id")
    private UUID facturaId;

    @Column(name = "fecha_cosecha", nullable = false, updatable = false)
    private LocalDateTime fechaCosecha = LocalDateTime.now();

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion = LocalDateTime.now();
}