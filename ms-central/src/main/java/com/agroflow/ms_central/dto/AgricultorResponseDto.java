package com.agroflow.ms_central.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgricultorResponseDto {
    private UUID agricultorId;
    private String nombre;
    private String apellidos;
    private String cedula;
    private String telefono;
    private String email;
    private String direccion;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}