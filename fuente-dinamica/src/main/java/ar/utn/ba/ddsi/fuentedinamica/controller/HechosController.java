package ar.utn.ba.ddsi.fuentedinamica.controller;

import ar.utn.ba.ddsi.fuentedinamica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.fuentedinamica.service.IHechosService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/hechos")
@CrossOrigin(origins = "http://localhost:3000")
public class HechosController {
  @Autowired
  private IHechosService hechosService;

  @GetMapping
  public List<HechoOutputDTO> buscarTodosLosHechos() {
    return this.hechosService.buscarTodos();
  }
}
