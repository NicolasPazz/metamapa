package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.service.impl.ColeccionesService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

  private final ColeccionesService coleccionesService;

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
   */
  @GetMapping("/colecciones/{handle}/hechos")
  public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      @PathVariable String handle,
      @RequestParam(required = false) String categoria,
      @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_reporte_desde,
      @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_reporte_hasta,
      @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_acontecimiento_desde,
      @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate fecha_acontecimiento_hasta,
      @RequestParam(required = false) String ubicacion
  ) {
    return coleccionesService.getHechosColeccionByHandle(
        handle,
        categoria,
        fecha_reporte_desde, fecha_reporte_hasta,
        fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
        ubicacion
    );
  }
}
