package com.agroflow.ms_inventario.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.agroflow.ms_inventario.entity.Insumo;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, UUID> {
    
    Optional<Insumo> findByNombreInsumo(String nombreInsumo);
    
    List<Insumo> findByActivoTrue();
    
    @Query("SELECT i FROM Insumo i WHERE i.stock <= i.stockMinimo AND i.activo = true")
    List<Insumo> findInsumosConStockBajo();
}