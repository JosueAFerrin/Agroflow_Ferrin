package com.agroflow.ms_central.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agroflow.ms_central.entity.Cosecha;

@Repository
public interface CosechaRepository extends JpaRepository<Cosecha, UUID> {
    
    @Query("SELECT c FROM Cosecha c JOIN FETCH c.agricultor WHERE c.cosechaId = :cosechaId")
    Cosecha findByIdWithAgricultor(@Param("cosechaId") UUID cosechaId);
}