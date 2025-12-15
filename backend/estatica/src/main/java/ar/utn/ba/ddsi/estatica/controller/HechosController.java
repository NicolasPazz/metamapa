package ar.utn.ba.ddsi.estatica.controller;

import ar.utn.ba.ddsi.estatica.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.estatica.service.HechosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/hechos")
@RequiredArgsConstructor
public class HechosController {

  private final HechosService hechosService;

  @GetMapping("/nuevos")
  public ResponseEntity<List<HechoOutputDTO>> getHechosNuevos() {
    List<HechoOutputDTO> hechosPendientes = hechosService.obtenerHechosNuevos();
    return ResponseEntity.ok(hechosPendientes);
  }
}




