package com.agroflow.ms_central.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAgricultorRequestDto {
    private String nombre;
    private String apellidos;
    private String cedula;
    private String telefono;
    private String email;
    private String direccion;
}