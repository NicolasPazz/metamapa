package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.service.HechosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/hechos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HechosController {

    private final HechosService hechosService;

    @GetMapping
    public ResponseEntity<List<HechoDTO>> getAllHechosVisibles(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_reporte_desde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_reporte_hasta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_acontecimiento_desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_acontecimiento_hasta,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) String fuente) {

        log.info("[HechosController] GET /api/hechos - categoria: {}, fecha_reporte_desde: {}, fecha_reporte_hasta: {}, fecha_acontecimiento_desde: {}, fecha_acontecimiento_hasta: {}, ubicacion: {}, fuente: {}",
                categoria, fecha_reporte_desde, fecha_reporte_hasta, fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion, fuente);

        return hechosService.getAll(
                categoria,
                fecha_reporte_desde, fecha_reporte_hasta,
                fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
                ubicacion, fuente);
    }

    @PatchMapping("/{id}/ocultar")
    public ResponseEntity<?> ocultarHecho(@PathVariable Long id) {
        log.info("[HechosController] PATCH /api/hechos/{}/ocultar", id);
        return hechosService.ocultarHecho(id);
    }
}

