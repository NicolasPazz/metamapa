package ar.utn.ba.ddsi.estatica.controller;

import ar.utn.ba.ddsi.estatica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.estatica.service.IFuenteService;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/hechos")
public class FuenteController {
  private final IFuenteService fuenteService;

  public FuenteController(IFuenteService hechosService) {
    this.fuenteService = hechosService;
  }

  /**
   * Devuelve todos los hechos que todavia no se habian enviado.
   *
   * @return lista con hechos nuevos
   */
  @GetMapping("/nuevos")
  public ResponseEntity<List<HechoOutputDTO>> getAllHechosNuevos() {
    List<HechoOutputDTO> hechosNuevos = this.fuenteService.getAllHechosNuevos();
    return ResponseEntity.ok(hechosNuevos);
  }
  @GetMapping("/hechos")
  public ResponseEntity<List<HechoOutputDTO>> getAllHechosVisibles() {
    List<HechoOutputDTO> hechosNuevos = this.fuenteService.getAllHechosNuevos();
    return ResponseEntity.ok(hechosNuevos);
  }
  @PostMapping(value = "/api/carga-masiva", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> recibirCsv(@RequestPart("file") MultipartFile file) {

    return ResponseEntity.ok("Recibido");
  }


}
