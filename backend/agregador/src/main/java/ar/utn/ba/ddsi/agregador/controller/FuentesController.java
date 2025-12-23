package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.dto.FuenteDTO;
import ar.utn.ba.ddsi.agregador.service.FuentesService;
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

    private final FuentesService fuentesService;

    @GetMapping
    public ResponseEntity<List<FuenteDTO>> getAllFuentes() {
        log.info("[FuentesController] - GET - /api/fuentes");
        return fuentesService.getAllFuentes();
    }
}

