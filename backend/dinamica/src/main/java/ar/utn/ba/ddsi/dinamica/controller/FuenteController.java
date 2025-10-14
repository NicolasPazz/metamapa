package ar.utn.ba.ddsi.dinamica.controller;

import ar.utn.ba.ddsi.dinamica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.dinamica.service.IFuenteService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hechos")
@CrossOrigin(origins = "${frontend.url}")
public class FuenteController {
  private final IFuenteService fuenteService;

  public FuenteController(IFuenteService fuenteService) {
    this.fuenteService = fuenteService;
  }

  @GetMapping
  public ResponseEntity<List<HechoOutputDTO>> getAllHechosVisibles(
      @RequestParam(required = false)
      String categoria,

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

      @RequestParam(required = false)
      String ubicacion) {

      List<HechoOutputDTO> hechos = this.fuenteService.getAllHechosVisibles(categoria, fecha_reporte_desde,
        fecha_reporte_hasta, fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion);
      return ResponseEntity.ok(hechos);
  }

  @GetMapping("/nuevos")
  public ResponseEntity<List<HechoOutputDTO>> getAllHechosNuevos() {
    List<HechoOutputDTO> hechosNuevos = this.fuenteService.getAllHechosNuevos();
    return ResponseEntity.ok(hechosNuevos);
  }
}
