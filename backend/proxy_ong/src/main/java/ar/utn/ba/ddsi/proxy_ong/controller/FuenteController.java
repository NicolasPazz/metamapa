package ar.utn.ba.ddsi.proxy_ong.controller;

import ar.utn.ba.ddsi.proxy_ong.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.proxy_ong.service.IFuenteService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/hechos")
public class FuenteController {
  private final IFuenteService fuenteService;

  public FuenteController(IFuenteService fuenteService) {
    this.fuenteService = fuenteService;
  }

  /**
   * Devuelve todos los hechos que todavia no se habian enviado.
   * @return lista reactiva (Mono) con hechos nuevos
   */
  @GetMapping("/nuevos")
  public ResponseEntity<List<HechoOutputDTO>> getAllHechosNuevos() {
    return fuenteService.getAllHechosNuevos();
  }
}
