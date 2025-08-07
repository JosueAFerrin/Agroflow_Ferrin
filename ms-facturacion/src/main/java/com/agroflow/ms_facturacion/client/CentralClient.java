package com.agroflow.ms_facturacion.client;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.agroflow.ms_facturacion.dto.UpdateEstadoCosechaDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CentralClient {

    private final RestTemplate restTemplate;

    @Value("${agroflow.ms-central.url:http://localhost:8081}")
    private String msCentralUrl;

    public boolean actualizarEstadoCosecha(UUID cosechaId, String estado, UUID facturaId) {
        try {
            String url = msCentralUrl + "/cosechas/" + cosechaId + "/estado";
            
            UpdateEstadoCosechaDto request = new UpdateEstadoCosechaDto();
            request.setEstado(estado);
            request.setFacturaId(facturaId);

            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<UpdateEstadoCosechaDto> entity = new HttpEntity<>(request, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.PUT, 
                entity, 
                String.class
            );

            boolean exitoso = response.getStatusCode().is2xxSuccessful();
            log.info("Actualización de estado en ms-central: {} - Status: {} - Cosecha: {}", 
                    exitoso ? "EXITOSA" : "FALLIDA", response.getStatusCode(), cosechaId);
            
            return exitoso;

        } catch (Exception e) {
            log.error("Error actualizando estado de cosecha {} en ms-central: {}", cosechaId, e.getMessage(), e);
            return false;
        }
    }
}