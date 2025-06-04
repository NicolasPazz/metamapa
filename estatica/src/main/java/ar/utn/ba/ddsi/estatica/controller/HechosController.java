package ar.utn.ba.ddsi.estatica.controller;


import ar.utn.ba.ddsi.estatica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.estatica.service.IHechosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("api/hechos")
public class HechosController {
    private final IHechosService hechosService;

    public HechosController(IHechosService hechosService) {
        this.hechosService = hechosService;
    }

    @GetMapping
    public ResponseEntity<List<HechoOutputDTO>> buscarTodosLosHechos() {
        List<HechoOutputDTO> hechos = this.hechosService.buscarTodos();
        return ResponseEntity.ok(hechos);
    }
}
