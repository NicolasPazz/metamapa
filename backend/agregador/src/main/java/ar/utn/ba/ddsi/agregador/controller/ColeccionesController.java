package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.domain.coleccion.ModoNavegacion;
import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.dto.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.service.ColeccionesService;
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
@RequestMapping("/api/colecciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ColeccionesController {

    private final ColeccionesService coleccionesService;

    @GetMapping
    public ResponseEntity<List<ColeccionDTO>> getAllColecciones() {
        log.info("[ColeccionesController] GET /api/colecciones (todas: locales + externas)");
        return coleccionesService.getAllColecciones();
    }

    @GetMapping("/{handle}/hechos")
    public ResponseEntity<List<HechoDTO>> getHechosDeColeccion(
            @PathVariable String handle,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_reporte_desde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_reporte_hasta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_acontecimiento_desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_acontecimiento_hasta,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) ModoNavegacion modo) {

        log.info("[ColeccionesController] GET /api/colecciones/{}/hechos", handle);

        return coleccionesService.getHechosColeccionByHandle(
                handle,
                categoria,
                fecha_reporte_desde,
                fecha_reporte_hasta,
                fecha_acontecimiento_desde,
                fecha_acontecimiento_hasta,
                ubicacion,
                modo
        );
    }
}

