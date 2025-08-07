package com.agroflow.ms_inventario.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agroflow.ms_inventario.dto.InsumoResponseDto;
import com.agroflow.ms_inventario.service.InventarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping("/insumos")
    public ResponseEntity<List<InsumoResponseDto>> obtenerTodosLosInsumos() {
        List<InsumoResponseDto> response = inventarioService.obtenerTodosLosInsumos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/insumos/stock-bajo")
    public ResponseEntity<List<InsumoResponseDto>> obtenerInsumosConStockBajo() {
        List<InsumoResponseDto> response = inventarioService.obtenerInsumosConStockBajo();
        return ResponseEntity.ok(response);
    }
}