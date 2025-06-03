package ar.utn.ba.ddsi.proxy_metamapa.controller;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.proxy_metamapa.service.impl.FuenteService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
  public List<HechoOutputDTO> getAllHechosVisibles() {
    return fuenteService.getAllHechosVisibles();
  }

  @GetMapping("/nuevos")
  public Mono<List<HechoOutputDTO>> getAllHechosNuevos() {
    return fuenteService.getAllHechosNuevos();
  }
}
