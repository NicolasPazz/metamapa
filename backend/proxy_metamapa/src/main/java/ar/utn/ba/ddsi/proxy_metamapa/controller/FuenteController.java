package ar.utn.ba.ddsi.proxy_metamapa.controller;

import ar.utn.ba.ddsi.proxy_metamapa.client.AgregadorClient;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.ColeccionDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.HechoDTO;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FuenteController {
  private final AgregadorClient agregador;

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
    return agregador.getAllHechosVisibles(
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
  public ResponseEntity<List<ColeccionDTO>> getAllColecciones() {
    return agregador.getAllColecciones();
  }

  /**
   * GET /colecciones/:identificador/hechos
   *   Esta ruta, permite obtener los hechos asociados a una colección. Acepta los mismos parámetros y devuelve los resultados en el mismo formato.
   */
  @GetMapping("/colecciones/{handle}/hechos")
  public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      @PathVariable("handle") String handle,
      @RequestParam(required = false) String categoria,

      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_reporte_desde,

      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_reporte_hasta,

      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_acontecimiento_desde,

      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_acontecimiento_hasta,

      @RequestParam(required = false) String ubicacion
  ) {
    return agregador.getHechosColeccionByHandle(
        handle,
        categoria,
        fecha_reporte_desde, fecha_reporte_hasta,
        fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
        ubicacion
    );
  }
}
