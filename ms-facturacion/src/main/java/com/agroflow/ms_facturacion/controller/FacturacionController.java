package com.agroflow.ms_facturacion.controller;

import com.agroflow.ms_facturacion.dto.FacturaResponseDto;
import com.agroflow.ms_facturacion.service.FacturacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/facturas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FacturacionController {

    private final FacturacionService facturacionService;

    @GetMapping
    public ResponseEntity<List<FacturaResponseDto>> obtenerTodasLasFacturas() {
        List<FacturaResponseDto> response = facturacionService.obtenerTodasLasFacturas();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{facturaId}")
    public ResponseEntity<FacturaResponseDto> obtenerFactura(@PathVariable UUID facturaId) {
        FacturaResponseDto response = facturacionService.obtenerFacturaPorId(facturaId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cosecha/{cosechaId}")
    public ResponseEntity<FacturaResponseDto> obtenerFacturaPorCosecha(@PathVariable UUID cosechaId) {
        FacturaResponseDto response = facturacionService.obtenerFacturaPorCosecha(cosechaId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<FacturaResponseDto>> obtenerFacturasPendientes() {
        List<FacturaResponseDto> response = facturacionService.obtenerFacturasPendientes();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{facturaId}/pagar")
    public ResponseEntity<FacturaResponseDto> marcarComoPagada(@PathVariable UUID facturaId) {
        FacturaResponseDto response = facturacionService.marcarComoPagada(facturaId);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }
}