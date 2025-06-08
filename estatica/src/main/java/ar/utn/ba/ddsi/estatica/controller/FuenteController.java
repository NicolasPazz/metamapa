package ar.utn.ba.ddsi.estatica.controller;


import ar.utn.ba.ddsi.estatica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.estatica.service.IFuenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/hechos")
public class FuenteController {
    private final IFuenteService fuenteService;

    public FuenteController(IFuenteService hechosService) {
        this.fuenteService = hechosService;
    }

    /**
     * Devuelve todos los hechos que todavia no se habian enviado.
     * @return lista reactiva (Mono) con hechos nuevos
     */
    @GetMapping("/nuevos")
    public ResponseEntity<List<HechoOutputDTO>> getAllHechosNuevos() {
        List<HechoOutputDTO> hechosNuevos = this.fuenteService.getAllHechosNuevos();
        return ResponseEntity.ok(hechosNuevos);
    }
}
