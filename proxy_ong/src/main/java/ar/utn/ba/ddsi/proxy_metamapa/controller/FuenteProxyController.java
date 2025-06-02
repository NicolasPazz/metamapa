package ar.utn.ba.ddsi.proxy_metamapa.controller;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.external.HechoProxyDTO;
import ar.utn.ba.ddsi.proxy_metamapa.service.impl.FuenteProxyONGService;
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
  private final FuenteProxyONGService fuenteService;

  public FuenteProxyController(FuenteProxyONGService fuenteService) {
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
