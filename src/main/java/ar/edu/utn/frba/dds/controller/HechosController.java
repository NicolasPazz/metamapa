package ar.edu.utn.frba.dds.controller;

import ar.edu.utn.frba.dds.domain.dto.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.service.IHechosService;
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
