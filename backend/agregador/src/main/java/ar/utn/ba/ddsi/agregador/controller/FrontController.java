package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.service.CategoriasService;
import ar.utn.ba.ddsi.agregador.service.HechosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/front")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FrontController {

    private final CategoriasService categoriasService;
    private final HechosService hechosService;

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> getAllCategorias() {
        log.info("[FrontController] GET /api/front/categorias");
        return categoriasService.getAllCategoriasNombres();
    }

    @GetMapping("/provincias")
    public ResponseEntity<List<String>> getProvinciasConHechos() {
        log.info("[FrontController] GET /api/front/provincias");
        return hechosService.getProvinciasConHechos();
    }
}

