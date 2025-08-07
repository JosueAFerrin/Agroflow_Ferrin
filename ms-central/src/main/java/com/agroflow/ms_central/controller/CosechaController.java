package com.agroflow.ms_central.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agroflow.ms_central.dto.CosechaResponseDto;
import com.agroflow.ms_central.dto.CreateCosechaRequestDto;
import com.agroflow.ms_central.dto.UpdateEstadoCosechaRequestDto;
import com.agroflow.ms_central.service.CosechaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cosechas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CosechaController {

    private final CosechaService cosechaService;

    @PostMapping
    public ResponseEntity<CosechaResponseDto> crearCosecha(@RequestBody CreateCosechaRequestDto request) {
        CosechaResponseDto response = cosechaService.crearCosecha(request);
        if (response == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CosechaResponseDto>> obtenerTodasLasCosechas() {
        List<CosechaResponseDto> response = cosechaService.obtenerTodasLasCosechas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{cosechaId}")
    public ResponseEntity<CosechaResponseDto> obtenerCosecha(@PathVariable UUID cosechaId) {
        CosechaResponseDto response = cosechaService.obtenerCosechaPorId(cosechaId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    // Nuevo endpoint para que ms-facturacion actualice el estado
    @PutMapping("/{cosechaId}/estado")
    public ResponseEntity<CosechaResponseDto> actualizarEstadoCosecha(
            @PathVariable UUID cosechaId,
            @RequestBody UpdateEstadoCosechaRequestDto request) {
        
        CosechaResponseDto response = cosechaService.actualizarEstadoCosecha(
            cosechaId, 
            request.getEstado(), 
            request.getFacturaId()
        );
        
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}