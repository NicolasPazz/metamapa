package ar.utn.ba.ddsi.proxy_ong.controller;

import ar.utn.ba.ddsi.proxy_ong.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.proxy_ong.service.FuenteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@Slf4j
@RestController
@RequestMapping("/api/hechos")
@RequiredArgsConstructor
public class FuenteController {
  private final FuenteService fuenteService;

  @GetMapping("/nuevos")
  public ResponseEntity<List<HechoOutputDTO>> getAllHechosNuevos() {
    log.info("[FuenteController] - GET - /api/hechos/nuevos");
    return fuenteService.getAllHechosNuevos();
  }
}
