package ar.utn.ba.ddsi.estadistica.controller;

import ar.utn.ba.ddsi.estadistica.dto.output.EstadisticasCompletasDTO;
import ar.utn.ba.ddsi.estadistica.dto.output.SpamDTO;
import ar.utn.ba.ddsi.estadistica.service.CsvExportService;
import ar.utn.ba.ddsi.estadistica.service.EstadisticasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/estadisticas")
@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
public class EstadisticasController {

  private final EstadisticasService estadisticasService;
  private final CsvExportService csvExportService;

  @GetMapping
  public ResponseEntity<EstadisticasCompletasDTO> obtenerEstadisticas() {
    log.info("[EstadisticasController] - GET - /api/estadisticas");
    try {
      EstadisticasCompletasDTO estadisticas = estadisticasService.obtenerEstadisticasCompletas();
      return ResponseEntity.ok(estadisticas);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/solicitudes-spam")
  public ResponseEntity<SpamDTO> obtenerEstadisticasSpam() {
    log.info("[EstadisticasController] - GET - /api/estadisticas/solicitudes-spam");
    try {
      SpamDTO resultado = estadisticasService.obtenerEstadisticasSpam();
      return ResponseEntity.ok(resultado);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/export/csv")
  public ResponseEntity<String> exportarEstadisticasCSV() {
    log.info("[EstadisticasController] - GET - /api/estadisticas/export/csv");
    try {
      String csv = csvExportService.exportarEstadisticasACSV();

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
      headers.setContentDispositionFormData("attachment", "estadisticas-metamapa.csv");

      return ResponseEntity.ok()
          .headers(headers)
          .body(csv);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error al generar CSV: " + e.getMessage());
    }
  }

  @PostMapping("/recalcular")
  public ResponseEntity<String> recalcularEstadisticas() {
    log.info("[EstadisticasController] - POST - /api/estadisticas/recalcular");
    try {
      estadisticasService.recalcularTodasLasEstadisticas();
      return ResponseEntity.ok("Estadísticas recalculadas exitosamente");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error al recalcular estadísticas: " + e.getMessage());
    }
  }
}

