package ar.utn.ba.ddsi.estatica.controller;

import ar.utn.ba.ddsi.estatica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.estatica.service.IFuenteService;

import java.time.LocalDate;
import java.util.List;

import ar.utn.ba.ddsi.estatica.service.impl.FuenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/hechos")
@RequiredArgsConstructor
public class FuenteController {
  private final FuenteService fuenteService;




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
  @PostMapping(value = "/carga-masiva", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> recibirCsv(@RequestPart("archivo") MultipartFile file) {
    if (file.isEmpty()) {
      return ResponseEntity.badRequest().body("Archivo vacio");
    }
    try {
      fuenteService.parsearMultipartAHechos(file); //
      return ResponseEntity.ok("Archivo processado");
    }
    catch (Exception e) {
      return ResponseEntity.badRequest().body("Error: " + e.getMessage());
    }
  }


}
