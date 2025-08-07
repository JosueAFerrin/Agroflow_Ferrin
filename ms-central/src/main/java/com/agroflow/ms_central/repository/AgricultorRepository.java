package com.agroflow.ms_central.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.agroflow.ms_central.entity.Agricultor;

@Repository
public interface AgricultorRepository extends JpaRepository<Agricultor, UUID> {
}