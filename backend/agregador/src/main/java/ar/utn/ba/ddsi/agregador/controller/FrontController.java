package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.dto.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.dto.PageResponse;
import ar.utn.ba.ddsi.agregador.service.AgregadorService;
import ar.utn.ba.ddsi.agregador.service.ColeccionesService;
import ar.utn.ba.ddsi.agregador.service.HechosService;
import ar.utn.ba.ddsi.agregador.service.CategoriasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/front")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FrontController {

    private final HechosService hechosService;
    private final ColeccionesService coleccionesService;
    private final CategoriasService categoriasService;
    private final AgregadorService agregadorService;

    @GetMapping("/hechos")
    public ResponseEntity<PageResponse<HechoDTO>> getHechosPaginados(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_reporte_desde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_reporte_hasta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_acontecimiento_desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_acontecimiento_hasta,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) String fuente,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        log.info("[FrontController] - GET - /api/front/hechos - categoria: {}, fecha_reporte_desde: {}, fecha_reporte_hasta: {}, fecha_acontecimiento_desde: {}, fecha_acontecimiento_hasta: {}, ubicacion: {}, fuente: {}, page: {}, size: {}",
                categoria, fecha_reporte_desde, fecha_reporte_hasta, fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion, fuente, page, size);

        PageResponse<HechoDTO> response = hechosService.getPaginatedHechos(
                categoria, fecha_reporte_desde, fecha_reporte_hasta,
                fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
                ubicacion, fuente, page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/colecciones")
    public ResponseEntity<PageResponse<ColeccionDTO>> getColeccionesPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        log.info("[FrontController] - GET - /api/front/colecciones - page: {}, size: {}", page, size);

        PageResponse<ColeccionDTO> response = coleccionesService.getPaginatedColecciones(page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/colecciones/{handle}/hechos")
    public ResponseEntity<PageResponse<HechoDTO>> getHechosColeccionPaginados(
            @PathVariable String handle,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_reporte_desde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_reporte_hasta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_acontecimiento_desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fecha_acontecimiento_hasta,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) String fuente,
            @RequestParam(required = false) String modo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {

        log.info("[FrontController] - GET - /api/front/colecciones/{}/hechos - handle: {}, categoria: {}, fecha_reporte_desde: {}, fecha_reporte_hasta: {}, fecha_acontecimiento_desde: {}, fecha_acontecimiento_hasta: {}, ubicacion: {}, fuente: {}, modo: {}, page: {}, size: {}",
                handle, handle, categoria, fecha_reporte_desde, fecha_reporte_hasta, fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion, fuente, modo, page, size);

        return coleccionesService.getHechosColeccionPaginated(
                handle, ubicacion, fecha_reporte_desde, fecha_reporte_hasta,
                fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
                categoria, fuente, modo, page, size);
    }

    @GetMapping("/hecho-reciente")
    public ResponseEntity<HechoDTO> getHechoReciente() {
        log.info("[FrontController] - GET - /api/front/hecho-reciente");
        return hechosService.getOneHechoReciente();
    }

    @GetMapping("/coleccion-reciente")
    public ResponseEntity<ColeccionDTO> getColeccionReciente() {
        log.info("[FrontController] - GET - /api/front/coleccion-reciente");
        return coleccionesService.getOneColeccion();
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> getCategorias() {
        log.info("[FrontController] - GET - /api/front/categorias");
        return categoriasService.getAllCategoriasNombres();
    }

    @GetMapping("/provincias")
    public ResponseEntity<List<String>> getProvincias() {
        log.info("[FrontController] - GET - /api/front/provincias");
        return hechosService.getProvinciasConHechos();
    }

    @GetMapping("/fuentes")
    public ResponseEntity<List<String>> getFuentes() {
        log.info("[FrontController] - GET - /api/front/fuentes");
        return hechosService.getFuentesConHechos();
    }

    @PostMapping("/ejecutar-scheduler")
    public ResponseEntity<Map<String, Object>> ejecutarScheduler(
            @RequestParam(required = false, defaultValue = "true") boolean obtenerHechosNuevos,
            @RequestParam(required = false, defaultValue = "true") boolean recalcularCriterios,
            @RequestParam(required = false, defaultValue = "true") boolean recalcularConsenso) {
        
        log.info("[FrontController] - POST - /api/front/ejecutar-scheduler - obtenerHechosNuevos: {}, recalcularCriterios: {}, recalcularConsenso: {}", 
                obtenerHechosNuevos, recalcularCriterios, recalcularConsenso);
        
        try {
            Map<String, Object> resultado = agregadorService.ejecutarScheduler(
                    obtenerHechosNuevos, recalcularCriterios, recalcularConsenso);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("exito", false);
            errorResult.put("error", e.getMessage());
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
}
