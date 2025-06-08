package ar.utn.ba.ddsi.controller;

import ar.utn.ba.ddsi.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.service.impl.FuenteService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/hechos")
public class FuenteController {
  private final FuenteService fuenteService;

  public FuenteController(FuenteService fuenteService) {
    this.fuenteService = fuenteService;
  }

  /**
   * Devuelve todos los hechos que todavia no se habian enviado.
   * @return lista reactiva (Mono) con hechos nuevos
   */
  @GetMapping("/nuevos")
  public Mono<List<HechoOutputDTO>> getAllHechosNuevos() {
    return fuenteService.getAllHechosNuevos();
  }
}
