package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.agregador.service.ISeederService;
import ar.utn.ba.ddsi.agregador.service.impl.ColeccionesService;
import ar.utn.ba.ddsi.agregador.service.impl.HechosService;
import ar.utn.ba.ddsi.agregador.service.impl.SolicitudesService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AgregadorController {
    private final SolicitudesService solicitudesService;
    private final HechosService hechosService;
    private final ColeccionesService coleccionesService;
    private final ISeederService seederService;


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
    public ResponseEntity<List<HechoDTO>> getAllHechosVisibles(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_reporte_desde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_reporte_hasta,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_acontecimiento_desde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_acontecimiento_hasta,
            @RequestParam(required = false) String ubicacion) {
        return hechosService.getAll(
                categoria,
                fecha_reporte_desde, fecha_reporte_hasta,
                fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
                ubicacion);
    }




    /**
     *   GET /colecciones
     *   Esta ruta expone todas las colecciones disponibles, independientemente del origen de sus fuentes.
     */
    @GetMapping("/colecciones")
    public ResponseEntity<List<ColeccionDTO>> getAllColecciones() { return this.coleccionesService.getAll(); }

    /**
     * GET /colecciones/:handle/hechos
     * Esta ruta permite obtener los hechos asociados a una colección,
     * aplicando filtros opcionales y seleccionando el modo de navegación.
     *
     * @param handle identificador de la colección
     * @param categoria filtro opcional por categoría
     * @param fecha_reporte_desde filtro opcional (formato: dd-MM-yyyy)
     * @param fecha_reporte_hasta filtro opcional (formato: dd-MM-yyyy)
     * @param fecha_acontecimiento_desde filtro opcional (formato: dd-MM-yyyy)
     * @param fecha_acontecimiento_hasta filtro opcional (formato: dd-MM-yyyy)
     * @param ubicacion filtro opcional por lat,long
     * @param modo modo de navegación (IRRESTRICTA o CURADA)
     */
    @GetMapping("/colecciones/{handle}/hechos")
    public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
        @PathVariable String handle,
        @RequestParam(required = false) String categoria,
        @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_reporte_desde,
        @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_reporte_hasta,
        @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_acontecimiento_desde,
        @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_acontecimiento_hasta,
        @RequestParam(required = false) String ubicacion,
        @RequestParam(required = false) String modo
    ) {
        return coleccionesService.getHechosColeccionByHandle(
            handle,
            categoria,
            fecha_reporte_desde, fecha_reporte_hasta,
            fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
            ubicacion, modo
        );
    }

    /**
     *   POST /solicitudes
     *   Permite crear solicitudes de eliminación.
     */
    @PostMapping("/solicitudes")
    public ResponseEntity<?> crear(@RequestBody SolicitudInputDTO solicitudInputDTO) {
      return solicitudesService.crear(solicitudInputDTO);

    }

    /*@PostMapping("/etiquetas")
    public ResponseEntity<HechoDTO> agregar(@RequestBody EtiquetaInputDTO etiquetaInputDTO) {
        // TODO: agregar etiquetas a hechos
    }*/

    @GetMapping("/inicializar")
    public Boolean inicializar() {
        this.seederService.inicializar();
        return true;
    }
}
