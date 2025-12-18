package ar.utn.ba.ddsi.proxy_ong.controller;

import ar.utn.ba.ddsi.proxy_ong.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.proxy_ong.service.FuenteService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/hechos")
public class FuenteController {
  private final FuenteService fuenteService;

  public FuenteController(FuenteService fuenteService) {
    this.fuenteService = fuenteService;
  }

  @GetMapping("/nuevos")
  public ResponseEntity<List<HechoOutputDTO>> getAllHechosNuevos() {
    return fuenteService.getAllHechosNuevos();
  }
}
