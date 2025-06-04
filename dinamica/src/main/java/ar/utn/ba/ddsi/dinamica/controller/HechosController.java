package ar.utn.ba.ddsi.dinamica.controller;


import ar.utn.ba.ddsi.dinamica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.dinamica.service.IHechosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<HechoOutputDTO>> buscarTodosLosHechos() {
        List<HechoOutputDTO> hechos = this.hechosService.buscarTodos();
        return ResponseEntity.ok(hechos);
    }
}
