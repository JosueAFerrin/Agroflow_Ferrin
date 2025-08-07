package com.agroflow.ms_central.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agroflow.ms_central.dto.AgricultorResponseDto;
import com.agroflow.ms_central.dto.CreateAgricultorRequestDto;
import com.agroflow.ms_central.service.AgricultorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/agricultores")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AgricultorController {

    private final AgricultorService agricultorService;

    @PostMapping
    public ResponseEntity<AgricultorResponseDto> crearAgricultor(@RequestBody CreateAgricultorRequestDto request) {
        AgricultorResponseDto response = agricultorService.crearAgricultor(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<AgricultorResponseDto>> obtenerTodosLosAgricultores() {
        List<AgricultorResponseDto> response = agricultorService.obtenerTodosLosAgricultores();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{agricultorId}")
    public ResponseEntity<AgricultorResponseDto> obtenerAgricultor(@PathVariable UUID agricultorId) {
        AgricultorResponseDto response = agricultorService.obtenerAgricultorPorId(agricultorId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}