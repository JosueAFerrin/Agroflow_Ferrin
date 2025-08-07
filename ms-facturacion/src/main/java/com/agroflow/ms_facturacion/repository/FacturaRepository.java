package com.agroflow.ms_facturacion.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.agroflow.ms_facturacion.entity.Factura;
import com.agroflow.ms_facturacion.util.EstadoFactura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, UUID> {
    
    Optional<Factura> findByCosechaId(UUID cosechaId);
    
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    
    List<Factura> findByEstado(EstadoFactura estado);
    
    @Query("SELECT f FROM Factura f WHERE f.fechaVencimiento < :fecha AND f.estado = 'PENDIENTE'")
    List<Factura> findFacturasVencidas(LocalDateTime fecha);
    
    @Query("SELECT COUNT(f) FROM Factura f WHERE DATE(f.fechaFactura) = DATE(:fecha)")
    Long countFacturasPorFecha(LocalDateTime fecha);
}