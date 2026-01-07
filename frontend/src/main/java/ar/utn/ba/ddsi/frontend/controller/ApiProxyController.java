package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ApiProxyController {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${app.api.agregador.base-url:http://localhost:8084/api}")
    private String agregadorBaseUrl;
    
    @PostMapping("/solicitudes")
    public ResponseEntity<?> crearSolicitudEliminacion(@RequestBody SolicitudEliminacionRequest request) {
        log.info("[ApiProxyController] - POST - /api/solicitudes - hechoId: {}, fuenteId: {}, idOrigen: {}, contribuyenteId: {}, textoFundamentos length: {}", 
                request.getHechoId(), request.getFuenteId(), request.getIdOrigen(), request.getContribuyenteId(), 
                request.getTextoFundamentos() != null ? request.getTextoFundamentos().length() : 0);
        try {
            // Enviar directamente al backend con el formato que espera (SolicitudEliminacionInputDTO)
            WebClient webClient = webClientBuilder.baseUrl(agregadorBaseUrl).build();
            
            SolicitudDTO resultado = webClient.post()
                    .uri("/solicitudes")
                    .bodyValue(request) // El request ya tiene el formato correcto con @JsonProperty
                    .retrieve()
                    .bodyToMono(SolicitudDTO.class)
                    .block();
            
            // Verificar si es spam y devolver mensaje apropiado
            if (resultado.getSpam() != null && resultado.getSpam()) {
                log.warn("[ApiProxyController] - Solicitud marcada como spam - ID: {}", resultado.getId());
                return ResponseEntity.status(400).body("Spam detectado. Tu solicitud ha sido rechazada automáticamente.");
            }
            
            return ResponseEntity.status(201).body(resultado);
        } catch (WebClientResponseException e) {
            log.error("[ApiProxyController] - Error del backend al crear solicitud: {} - Status: {}", e.getMessage(), e.getStatusCode());
            String errorMessage = e.getResponseBodyAsString();
            if (errorMessage != null && !errorMessage.isEmpty() && !errorMessage.trim().equals("null")) {
                return ResponseEntity.status(e.getStatusCode()).body(errorMessage);
            }
            return ResponseEntity.status(e.getStatusCode()).body("Error al crear la solicitud: " + e.getMessage());
        } catch (Exception e) {
            log.error("[ApiProxyController] - Error al crear solicitud: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error al crear la solicitud: " + e.getMessage());
        }
    }
    
    @Data
    static class SolicitudEliminacionRequest {
        @JsonProperty("hecho_id")
        private Long hechoId;
        
        @JsonProperty("fuente_id")
        private Long fuenteId;
        
        @JsonProperty("id_origen")
        private Long idOrigen;
        
        @JsonProperty("texto_fundamentos")
        private String textoFundamentos;
        
        @JsonProperty("contribuyente_id")
        private Long contribuyenteId;
    }
}

