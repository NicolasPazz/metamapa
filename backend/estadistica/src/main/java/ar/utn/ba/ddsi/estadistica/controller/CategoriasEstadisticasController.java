package ar.utn.ba.ddsi.estadistica.controller;

import ar.utn.ba.ddsi.estadistica.dto.output.CategoriaDominanteDTO;
import ar.utn.ba.ddsi.estadistica.dto.output.CategoriaHoraDTO;
import ar.utn.ba.ddsi.estadistica.dto.output.CategoriaProvinciaDTO;
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
public class CategoriasEstadisticasController {

  private final EstadisticasService estadisticasService;

  @GetMapping("/categoria-mas-hechos")
  public ResponseEntity<CategoriaDominanteDTO> obtenerCategoriaConMasHechos() {
    log.info("[CategoriasEstadisticasController] - GET - /api/estadisticas/categoria-mas-hechos");
    try {
      CategoriaDominanteDTO resultado = estadisticasService.obtenerCategoriaConMasHechos();
      if (resultado == null) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(resultado);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/categoria/{categoria}/provincia-mas-hechos")
  public ResponseEntity<CategoriaProvinciaDTO> obtenerProvinciaConMasHechosPorCategoria(
      @PathVariable String categoria) {
    log.info("[CategoriasEstadisticasController] - GET - /api/estadisticas/categoria/{}/provincia-mas-hechos - categoria: {}", categoria, categoria);
    try {
      CategoriaProvinciaDTO resultado = estadisticasService.obtenerProvinciaConMasHechosPorCategoria(categoria);
      if (resultado == null) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(resultado);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/categorias/provincia-mas-hechos")
  public ResponseEntity<List<CategoriaProvinciaDTO>> obtenerTodasLasCategoriasConProvinciaDominante() {
    log.info("[CategoriasEstadisticasController] - GET - /api/estadisticas/categorias/provincia-mas-hechos");
    try {
      List<CategoriaProvinciaDTO> resultados = estadisticasService.obtenerTodasLasCategoriasConProvinciaDominante();
      return ResponseEntity.ok(resultados);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/categoria/{categoria}/hora-mas-hechos")
  public ResponseEntity<CategoriaHoraDTO> obtenerHoraConMasHechosPorCategoria(
      @PathVariable String categoria) {
    log.info("[CategoriasEstadisticasController] - GET - /api/estadisticas/categoria/{}/hora-mas-hechos - categoria: {}", categoria, categoria);
    try {
      CategoriaHoraDTO resultado = estadisticasService.obtenerHoraConMasHechosPorCategoria(categoria);
      if (resultado == null) {
        return ResponseEntity.notFound().build();
      }
      return ResponseEntity.ok(resultado);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/categorias/hora-mas-hechos")
  public ResponseEntity<List<CategoriaHoraDTO>> obtenerTodasLasCategoriasConHoraDominante() {
    log.info("[CategoriasEstadisticasController] - GET - /api/estadisticas/categorias/hora-mas-hechos");
    try {
      List<CategoriaHoraDTO> resultados = estadisticasService.obtenerTodasLasCategoriasConHoraDominante();
      return ResponseEntity.ok(resultados);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}

