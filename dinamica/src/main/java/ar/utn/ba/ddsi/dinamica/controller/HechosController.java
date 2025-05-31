package ar.utn.ba.ddsi.dinamica.controller;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.HechoModificarInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.dinamica.service.IHechosService;
import ar.utn.ba.ddsi.dinamica.service.ISeederService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/hechos")
@CrossOrigin(origins = "http://localhost:3000")

public class HechosController {
  @Autowired
  private IHechosService hechosService;

  @Autowired
  private ISeederService seederService;

  @GetMapping
  public List<HechoOutputDTO> buscarTodosLosHechos() {

    return this.hechosService.buscarTodos();
  }

  @GetMapping("/inicializar")
  public Boolean inicializar(){
    this.seederService.inicializar();
    return true;
  }

  @PutMapping("/{id}")
  public ResponseEntity<HechoOutputDTO> modificarHecho(@PathVariable Long id, @RequestBody HechoModificarInputDTO input) {
    try {
      HechoOutputDTO modificado = this.hechosService.modificarHecho(id, input);
      return ResponseEntity.ok(modificado);
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(null); // O podés retornar un mensaje de error más detallado
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

}
