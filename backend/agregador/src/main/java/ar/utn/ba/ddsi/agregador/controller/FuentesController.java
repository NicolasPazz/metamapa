package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.dto.FuenteDTO;
import ar.utn.ba.ddsi.agregador.service.FuentesConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/fuentes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FuentesController {

    private final FuentesConfigService fuentesConfigService;

    @GetMapping
    public ResponseEntity<List<FuenteDTO>> getAllFuentes() {
        log.info("[FuentesController] GET /api/fuentes");
        return fuentesConfigService.getAllFuentes();
    }

    @GetMapping("/activas")
    public ResponseEntity<List<FuenteDTO>> getFuentesActivas() {
        log.info("[FuentesController] GET /api/fuentes/activas");
        return fuentesConfigService.getFuentesActivas();
    }
}

