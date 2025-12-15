package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.dto.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.service.ColeccionesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/colecciones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminColeccionesController {

    private final ColeccionesService coleccionesService;

    @PostMapping
    public ResponseEntity<?> crearColeccion(@RequestBody @Valid ColeccionDTO dto) {
        log.info("[AdminColeccionesController] POST /api/admin/colecciones");
        return coleccionesService.crearColeccion(dto);
    }

    @GetMapping
    public ResponseEntity<List<ColeccionDTO>> getAllColecciones() {
        log.info("[AdminColeccionesController] GET /api/admin/colecciones");
        return coleccionesService.getAll();
    }

    @PatchMapping("/{handle}")
    public ResponseEntity<?> actualizarColeccion(
            @PathVariable String handle,
            @RequestBody ColeccionDTO dto) {
        log.info("[AdminColeccionesController] PATCH /api/admin/colecciones/{}", handle);
        return coleccionesService.actualizarColeccion(handle, dto);
    }

    @DeleteMapping("/{handle}")
    public ResponseEntity<?> eliminarColeccion(@PathVariable String handle) {
        log.info("[AdminColeccionesController] DELETE /api/admin/colecciones/{}", handle);
        return coleccionesService.eliminarColeccion(handle);
    }
}


