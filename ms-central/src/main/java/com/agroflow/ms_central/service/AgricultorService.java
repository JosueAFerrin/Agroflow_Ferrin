package com.agroflow.ms_central.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.agroflow.ms_central.dto.AgricultorResponseDto;
import com.agroflow.ms_central.dto.CreateAgricultorRequestDto;
import com.agroflow.ms_central.entity.Agricultor;
import com.agroflow.ms_central.repository.AgricultorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgricultorService {

    private final AgricultorRepository agricultorRepository;

    public AgricultorResponseDto crearAgricultor(CreateAgricultorRequestDto request) {
        Agricultor agricultor = new Agricultor();
        agricultor.setNombre(request.getNombre());
        agricultor.setApellidos(request.getApellidos());
        agricultor.setCedula(request.getCedula());
        agricultor.setTelefono(request.getTelefono());
        agricultor.setEmail(request.getEmail());
        agricultor.setDireccion(request.getDireccion());
        agricultor.setActivo(true);
        agricultor.setFechaCreacion(LocalDateTime.now());
        agricultor.setFechaActualizacion(LocalDateTime.now());

        Agricultor agricultorGuardado = agricultorRepository.save(agricultor);
        return convertirAResponseDto(agricultorGuardado);
    }

    public List<AgricultorResponseDto> obtenerTodosLosAgricultores() {
        List<Agricultor> agricultores = agricultorRepository.findAll();
        return agricultores.stream()
                .map(this::convertirAResponseDto)
                .toList();
    }

    public AgricultorResponseDto obtenerAgricultorPorId(UUID agricultorId) {
        Agricultor agricultor = agricultorRepository.findById(agricultorId).orElse(null);
        if (agricultor == null) {
            return null;
        }
        return convertirAResponseDto(agricultor);
    }

    private AgricultorResponseDto convertirAResponseDto(Agricultor agricultor) {
        AgricultorResponseDto dto = new AgricultorResponseDto();
        dto.setAgricultorId(agricultor.getAgricultorId());
        dto.setNombre(agricultor.getNombre());
        dto.setApellidos(agricultor.getApellidos());
        dto.setCedula(agricultor.getCedula());
        dto.setTelefono(agricultor.getTelefono());
        dto.setEmail(agricultor.getEmail());
        dto.setDireccion(agricultor.getDireccion());
        dto.setActivo(agricultor.getActivo());
        dto.setFechaCreacion(agricultor.getFechaCreacion());
        return dto;
    }
}