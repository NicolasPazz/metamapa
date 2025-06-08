package ar.utn.ba.ddsi.controller;

import ar.utn.ba.ddsi.domain.dto.output.ColeccionDTO;
import ar.utn.ba.ddsi.domain.dto.output.HechoDTO;
import ar.utn.ba.ddsi.domain.dto.output.SolicitudDeEliminacionDTO;
import ar.utn.ba.ddsi.service.impl.FuenteService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class FuenteController {
  private final FuenteService fuenteService;

  public FuenteController(FuenteService fuenteService) {
    this.fuenteService = fuenteService;
  }

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
      @RequestParam(required = false) String fecha_reporte_desde,
      @RequestParam(required = false) String fecha_reporte_hasta,
      @RequestParam(required = false) String fecha_acontecimiento_desde,
      @RequestParam(required = false) String fecha_acontecimiento_hasta,
      @RequestParam(required = false) String ubicacion){
    return fuenteService.getAllHechosVisibles(categoria, fecha_reporte_desde, fecha_reporte_hasta, fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion);
  }

  /**
   *   GET /colecciones
   *   Esta ruta expone todas las colecciones disponibles, independientemente del origen de sus fuentes.
   */
  @GetMapping("/colecciones")
  public Mono<List<ColeccionDTO>> getAllColecciones() {
    return fuenteService.getAllColecciones();
  }

  /**
   * GET /colecciones/:identificador/hechos
   *   Esta ruta, permite obtener los hechos asociados a una colección. Acepta los mismos parámetros y devuelve los resultados en el mismo formato.
   */
  @GetMapping("/colecciones/{handle}/hechos")
  public Mono<ColeccionDTO> getColeccionByHandle(@PathVariable("handle") String handle) {
    return fuenteService.getColeccionByHandle(handle);
  }

  /**
   *   POST /solicitudes
   *   Permite crear solicitudes de eliminación, enviando los datos de la solicitud como un JSON a través del cuerpo (body) de la misma.
   */
  @PostMapping("/solicitudes")
  public Mono<SolicitudDeEliminacionDTO> crearSolicitudDeEliminacion(@RequestBody SolicitudDeEliminacionDTO solicitud) {
    return fuenteService.crearSolicitudDeEliminacion(solicitud);
  }
}
