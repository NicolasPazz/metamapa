package ar.utn.ba.ddsi.dinamica.controller;

import ar.utn.ba.ddsi.dinamica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.dinamica.service.FuenteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hechos")
@CrossOrigin(origins = "${frontend.url}")
@RequiredArgsConstructor
@Slf4j
public class FuenteController {
  private final FuenteService fuenteService;

  @GetMapping
  public ResponseEntity<List<HechoOutputDTO>> getAllHechosVisibles() {
    log.debug("GET /api/hechos - Retorna lista vacía intencionalmente");
    return ResponseEntity.ok(fuenteService.getAllHechosVisibles());
  }

  @GetMapping("/nuevos")
  public ResponseEntity<List<HechoOutputDTO>> getAllHechosNuevos() {
    log.info("GET /api/hechos/nuevos - Solicitando hechos nuevos para agregador");
    List<HechoOutputDTO> hechosNuevos = fuenteService.getAllHechosNuevos();
    log.info("Retornando {} hechos nuevos", hechosNuevos.size());
    return ResponseEntity.ok(hechosNuevos);
  }
}
