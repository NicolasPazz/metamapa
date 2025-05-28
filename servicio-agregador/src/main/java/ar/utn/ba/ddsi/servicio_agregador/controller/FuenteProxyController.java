package ar.utn.ba.ddsi.servicio_agregador.controller;

import ar.utn.ba.ddsi.servicio_agregador.domain.dto.external.HechoProxyDTO;
import ar.utn.ba.ddsi.servicio_agregador.service.impl.FuenteProxyService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/desastres")
public class FuenteProxyController implements IFuenteController{
  private final FuenteProxyService fuenteService;

  public FuenteProxyController(FuenteProxyService fuenteService) {
    this.fuenteService = fuenteService;
  }

  @GetMapping
  public Mono<List<HechoProxyDTO>> getAllHechos() {
    return fuenteService.getAllHechos();
  }

  @GetMapping("/filtered")
  public Mono<List<HechoProxyDTO>> getHechosWithParams(
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer per_page) {
    return fuenteService.getProductsWithParams(page, per_page);
  }

  @GetMapping("/{id}")
  public Mono<HechoProxyDTO> getProductById(@PathVariable Long id) {
    return fuenteService.getProductById(id);
  }
}
