package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.dto.CategoriaEstadisticaDTO;
import ar.utn.ba.ddsi.agregador.dto.EstadisticasDTO;
import ar.utn.ba.ddsi.agregador.dto.HoraEstadisticaDTO;
import ar.utn.ba.ddsi.agregador.dto.ProvinciaEstadisticaDTO;
import ar.utn.ba.ddsi.agregador.service.EstadisticasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    @GetMapping
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticasCompletas() {
        log.info("[EstadisticasController] GET /api/estadisticas - Obteniendo estadísticas completas");
        EstadisticasDTO estadisticas = estadisticasService.obtenerEstadisticas();
        return ResponseEntity.ok(estadisticas);
    }

    @PostMapping("/recalcular")
    public ResponseEntity<String> recalcularEstadisticas() {
        log.info("[EstadisticasController] POST /api/estadisticas/recalcular - Recalculando estadísticas");
        estadisticasService.recalcularEstadisticas();
        return ResponseEntity.ok("Estadísticas recalculadas exitosamente");
    }

    @GetMapping("/categoria-con-mas-hechos")
    public ResponseEntity<CategoriaEstadisticaDTO> obtenerCategoriaConMasHechos() {
        log.info("[EstadisticasController] GET /api/estadisticas/categoria-con-mas-hechos");
        CategoriaEstadisticaDTO resultado = estadisticasService.obtenerCategoriaConMasHechos();

        if (resultado == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/provincia-por-coleccion")
    public ResponseEntity<Map<String, ProvinciaEstadisticaDTO>> obtenerProvinciasPorColeccion() {
        log.info("[EstadisticasController] GET /api/estadisticas/provincia-por-coleccion");
        Map<String, ProvinciaEstadisticaDTO> resultado = estadisticasService.obtenerProvinciaConMasHechosPorColeccion();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/provincia-por-categoria")
    public ResponseEntity<Map<String, ProvinciaEstadisticaDTO>> obtenerProvinciasPorTodasCategorias() {
        log.info("[EstadisticasController] GET /api/estadisticas/provincia-por-categoria");
        Map<String, ProvinciaEstadisticaDTO> resultado = estadisticasService.obtenerProvinciaPorTodasLasCategorias();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/provincia-por-categoria/{categoria}")
    public ResponseEntity<ProvinciaEstadisticaDTO> obtenerProvinciaPorCategoria(
            @PathVariable String categoria) {
        log.info("[EstadisticasController] GET /api/estadisticas/provincia-por-categoria/{}", categoria);
        ProvinciaEstadisticaDTO resultado = estadisticasService.obtenerProvinciaConMasHechosPorCategoria(categoria);

        if (resultado == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/hora-pico-por-categoria")
    public ResponseEntity<Map<String, HoraEstadisticaDTO>> obtenerHorasPicoPorTodasCategorias() {
        log.info("[EstadisticasController] GET /api/estadisticas/hora-pico-por-categoria");
        Map<String, HoraEstadisticaDTO> resultado = estadisticasService.obtenerHoraPicoParaTodasLasCategorias();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/hora-pico-por-categoria/{categoria}")
    public ResponseEntity<HoraEstadisticaDTO> obtenerHoraPicoPorCategoria(
            @PathVariable String categoria) {
        log.info("[EstadisticasController] GET /api/estadisticas/hora-pico-por-categoria/{}", categoria);
        HoraEstadisticaDTO resultado = estadisticasService.obtenerHoraPicoPorCategoria(categoria);

        if (resultado == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/solicitudes-spam")
    public ResponseEntity<Map<String, Long>> obtenerTotalSolicitudesSpam() {
        log.info("[EstadisticasController] GET /api/estadisticas/solicitudes-spam");
        Long total = estadisticasService.obtenerTotalSolicitudesSpam();
        return ResponseEntity.ok(Map.of("total_spam", total));
    }

    @GetMapping(value = "/exportar/csv", produces = "text/csv")
    public ResponseEntity<String> exportarEstadisticasCSV() {
        log.info("[EstadisticasController] GET /api/estadisticas/exportar/csv - Exportando estadísticas a CSV");

        String csvContent = estadisticasService.exportarEstadisticasCSV();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "estadisticas.csv");

        return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
    }
}

