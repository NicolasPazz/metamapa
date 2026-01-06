package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.dtos.HechoSimpleDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.frontend.service.SolicitudService;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class ApiProxyController {
    
    private final SolicitudService solicitudService;
    
    @PostMapping("/solicitudes")
    public ResponseEntity<?> crearSolicitudEliminacion(@RequestBody SolicitudEliminacionRequest request) {
        log.info("[ApiProxyController] - POST - /api/solicitudes - hechoId: {}, fuenteId: {}, idOrigen: {}, contribuyenteId: {}, textoFundamentos length: {}", 
                request.getHechoId(), request.getFuenteId(), request.getIdOrigen(), request.getContribuyenteId(), 
                request.getTextoFundamentos() != null ? request.getTextoFundamentos().length() : 0);
        try {
            // Convertir el request a SolicitudDTO
            SolicitudDTO solicitudDTO = new SolicitudDTO();
            solicitudDTO.setTextoFundamentos(request.getTextoFundamentos());
            solicitudDTO.setIdContribuyente(request.getContribuyenteId());
            solicitudDTO.setFuenteId(request.getFuenteId());
            solicitudDTO.setIdOrigen(request.getIdOrigen());
            
            // Si hay hecho_id, crear un HechoSimpleDTO con ese ID
            if (request.getHechoId() != null) {
                HechoSimpleDTO hecho = new HechoSimpleDTO();
                hecho.setId(request.getHechoId());
                solicitudDTO.setHecho(hecho);
            }
            
            SolicitudDTO resultado = solicitudService.crearSolicitud(solicitudDTO);
            return ResponseEntity.ok(resultado);
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

