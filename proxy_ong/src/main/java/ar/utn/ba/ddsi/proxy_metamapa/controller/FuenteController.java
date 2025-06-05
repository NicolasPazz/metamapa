package ar.utn.ba.ddsi.proxy_metamapa.controller;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.proxy_metamapa.service.impl.FuenteService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("hechos")
public class FuenteController {
  private final FuenteService fuenteService;

  public FuenteController(FuenteService fuenteService) {
    this.fuenteService = fuenteService;
  }

  @GetMapping
  public List<HechoOutputDTO> getAllHechosVisibles(
      @RequestParam(required = false) String categoria,
      @RequestParam(required = false) String fecha_reporte_desde,
      @RequestParam(required = false) String fecha_reporte_hasta,
      @RequestParam(required = false) String fecha_acontecimiento_desde,
      @RequestParam(required = false) String fecha_acontecimiento_hasta,
      @RequestParam(required = false) String ubicacion){
    return fuenteService.getAllHechosVisibles(categoria, fecha_reporte_desde, fecha_reporte_hasta, fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion);
  }

  /**
   * Devuelve todos los hechos que todavia no se habian enviado.
   *
   * @return lista reactiva (Mono) con hechos nuevos
   */
  @GetMapping("/nuevos")
  public Mono<List<HechoOutputDTO>> getAllHechosNuevos() {
    return fuenteService.getAllHechosNuevos();
  }
}
