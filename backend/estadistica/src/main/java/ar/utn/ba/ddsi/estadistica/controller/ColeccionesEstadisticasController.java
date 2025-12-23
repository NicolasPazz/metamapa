package ar.utn.ba.ddsi.estadistica.controller;

import ar.utn.ba.ddsi.estadistica.dto.output.ColeccionProvinciaDTO;
import ar.utn.ba.ddsi.estadistica.service.EstadisticasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estadisticas")
@CrossOrigin(origins = "*")
@Slf4j
@RequiredArgsConstructor
public class ColeccionesEstadisticasController {

  private final EstadisticasService estadisticasService;

  @GetMapping("/coleccion/{handle}/provincia-mas-hechos")
  public ResponseEntity<ColeccionProvinciaDTO> obtenerProvinciaConMasHechosPorColeccion(
      @PathVariable String handle) {
    log.info("[ColeccionesEstadisticasController] - GET - /api/estadisticas/coleccion/{}/provincia-mas-hechos - handle: {}", handle, handle);
    try {
      ColeccionProvinciaDTO resultado = estadisticasService.obtenerProvinciaConMasHechosPorColeccion(handle);
      if (resultado == null) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(resultado);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/colecciones/provincia-mas-hechos")
  public ResponseEntity<List<ColeccionProvinciaDTO>> obtenerTodasLasColeccionesConProvinciaDominante() {
    log.info("[ColeccionesEstadisticasController] - GET - /api/estadisticas/colecciones/provincia-mas-hechos");
    try {
      List<ColeccionProvinciaDTO> resultados = estadisticasService.obtenerTodasLasColeccionesConProvinciaDominante();
      return ResponseEntity.ok(resultados);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}

