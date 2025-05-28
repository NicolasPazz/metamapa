package ar.utn.ba.ddsi.servicio_estadisticas.controller;

import ar.utn.ba.ddsi.servicio_estadisticas.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.servicio_estadisticas.service.IHechosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

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
