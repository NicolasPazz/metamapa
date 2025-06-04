package ar.utn.ba.ddsi.dinamica.controller;


import ar.utn.ba.ddsi.dinamica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.dinamica.service.IFuenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("api/hechos")
@CrossOrigin(origins = "http://localhost:3000")
public class FuenteController {
    private final IFuenteService fuenteService;

    public FuenteController(IFuenteService fuenteService) {
        this.fuenteService = fuenteService;
    }

    @GetMapping
    public ResponseEntity<List<HechoOutputDTO>> getAllHechosVisibles(
        @RequestParam(required = false) String categoria,
        @RequestParam(required = false) String fecha_reporte_desde,
        @RequestParam(required = false) String fecha_reporte_hasta,
        @RequestParam(required = false) String fecha_acontecimiento_desde,
        @RequestParam(required = false) String fecha_acontecimiento_hasta,
        @RequestParam(required = false) String ubicacion) {
        List<HechoOutputDTO> hechos = this.fuenteService.getAllHechosVisibles(categoria, fecha_reporte_desde, fecha_reporte_hasta, fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion);
        return ResponseEntity.ok(hechos);
    }

    @GetMapping("/nuevos")
    public ResponseEntity<List<HechoOutputDTO>> getAllHechosNuevos() {
        List<HechoOutputDTO> hechosNuevos = this.fuenteService.getAllHechosNuevos();
        return ResponseEntity.ok(hechosNuevos);
    }
}
