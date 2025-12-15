package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.dto.SolicitudEliminacionDTO;
import ar.utn.ba.ddsi.agregador.service.SolicitudesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/solicitudes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminSolicitudesController {

    private final SolicitudesService solicitudesService;

    @GetMapping
    public ResponseEntity<List<SolicitudEliminacionDTO>> getAllSolicitudes() {
        log.info("[AdminSolicitudesController] GET /api/admin/solicitudes");
        return solicitudesService.getAll();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> actualizarEstadoSolicitud(
            @PathVariable Long id,
            @RequestBody SolicitudEliminacionDTO dto) {
        log.info("[AdminSolicitudesController] PATCH /api/admin/solicitudes/{}", id);
        return solicitudesService.actualizarEstado(id, dto);
    }
}

