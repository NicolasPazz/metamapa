package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechosServiceDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.input.EtiquetaInputDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Etiqueta;
import ar.utn.ba.ddsi.agregador.service.impl.ColeccionesService;
import ar.utn.ba.ddsi.agregador.service.impl.HechosService;
import ar.utn.ba.ddsi.agregador.service.impl.SolicitudesService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AgregadorController {
    private final SolicitudesService solicitudesService;
    private final HechosService hechosService;
    private final ColeccionesService coleccionesService;

    /**
     * GET /hechos
     * Esta ruta expone todos los hechos del sistema y los devuelve como una lista en formato JSON.
     * Acepta parámetros opcionales para filtrar los resultados:
     *
     * @param categoria nombre de la categoría del hecho
     * @param fecha_reporte_desde fecha mínima de reporte (formato: dd-MM-yyyy)
     * @param fecha_reporte_hasta fecha máxima de reporte (formato: dd-MM-yyyy)
     * @param fecha_acontecimiento_desde fecha mínima de acontecimiento (formato: dd-MM-yyyy)
     * @param fecha_acontecimiento_hasta fecha máxima de acontecimiento (formato: dd-MM-yyyy)
     * @param ubicacion ubicación del hecho (formato: lat,log)
     * @return lista de hechos visibles según los filtros
     */
    @GetMapping("/hechos")
    public Mono<List<HechoDTO>> getAllHechosVisibles(
        @RequestParam(required = false) String categoria,

        @RequestParam(required = false)
        @DateTimeFormat(pattern = "dd-MM-yyyy")
        LocalDate fecha_reporte_desde,

        @RequestParam(required = false)
        @DateTimeFormat(pattern = "dd-MM-yyyy")
        LocalDate fecha_reporte_hasta,

        @RequestParam(required = false)
        @DateTimeFormat(pattern = "dd-MM-yyyy")
        LocalDate fecha_acontecimiento_desde,

        @RequestParam(required = false)
        @DateTimeFormat(pattern = "dd-MM-yyyy")
        LocalDate fecha_acontecimiento_hasta,

        @RequestParam(required = false) String ubicacion) {
        return hechosService.getAll(categoria, fecha_reporte_desde, fecha_reporte_hasta, fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion);
    }

    /**
     *   GET /colecciones
     *   Esta ruta expone todas las colecciones disponibles, independientemente del origen de sus fuentes.
     */
    @GetMapping("/colecciones")
    public Mono<List<ColeccionDTO>> getAllColecciones() { return this.coleccionesService.getAll(); }

    /**
     * GET /colecciones/:identificador/hechos
     *   Esta ruta, permite obtener los hechos asociados a una colección.
     */
    @GetMapping("/coleccion/{handle}/hechos")
    public Mono<List<HechoDTO>> getColeccionByHandle(@PathVariable String handle) {
        return this.coleccionesService.getByHandle(handle);
    }

    /**
     *   POST /solicitudes
     *   Permite crear solicitudes de eliminación.
     */
    @PostMapping("/solicitudes")
    public ResponseEntity<SolicitudOutputDTO> crear(@RequestBody SolicitudInputDTO solicitudInputDTO) {
        // y si el hecho pertenece a otra instancia metamapa quien la maneja?
        try {
            SolicitudOutputDTO respuesta = solicitudesService.crear(solicitudInputDTO);
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    /*@PostMapping("/etiquetas")
    public ResponseEntity<HechoOutputDTO> agregar(@RequestBody EtiquetaInputDTO etiquetaInputDTO) {
        // TODO: agregar etiquetas a hechos
    }*/
}
