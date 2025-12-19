package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.service.HechosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/hechos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminHechosController {

    private final HechosService hechosService;

    @PatchMapping("/{id}/etiquetas")
    public ResponseEntity<?> actualizarEtiquetas(
            @PathVariable Long id,
            @RequestBody List<String> etiquetas) {
        log.info("[AdminHechosController] PATCH /api/admin/hechos/{}/etiquetas - etiquetas: {}", id, etiquetas);
        return hechosService.actualizarEtiquetas(id, etiquetas);
    }
}

