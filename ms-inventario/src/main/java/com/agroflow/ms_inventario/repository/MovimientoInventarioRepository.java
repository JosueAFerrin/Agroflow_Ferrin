package com.agroflow.ms_inventario.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agroflow.ms_inventario.entity.MovimientoInventario;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, UUID> {
    
    List<MovimientoInventario> findByCosechaIdOrderByFechaMovimientoDesc(UUID cosechaId);
    
    List<MovimientoInventario> findByInsumo_InsumoIdOrderByFechaMovimientoDesc(UUID insumoId);
}