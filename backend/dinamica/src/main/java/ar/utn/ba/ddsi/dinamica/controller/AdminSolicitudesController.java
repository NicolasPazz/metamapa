package ar.utn.ba.ddsi.dinamica.controller;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarAdminInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.dinamica.service.SolicitudesService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/solicitudes")
@CrossOrigin(origins = "${frontend.url}")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminSolicitudesController {
  private final SolicitudesService solicitudesService;

  @GetMapping
  public ResponseEntity<List<SolicitudOutputDTO>> buscarTodasLasSolicitudes() {
    log.info("[AdminSolicitudesController] - GET - /api/admin/solicitudes");
    List<SolicitudOutputDTO> solicitudes = solicitudesService.buscarTodas();
    return ResponseEntity.ok(solicitudes);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<SolicitudOutputDTO> revisarSolicitud(
      @PathVariable Long id,
      @Valid @RequestBody SolicitudModificarAdminInputDTO input) {
    log.info("[AdminSolicitudesController] - PATCH - /api/admin/solicitudes/{} - id: {}, nuevoEstadoSolicitud: {}, nuevoTitulo: {}, nuevoDescripcion: {}, nuevoNombreCategoria: {}, nuevoLatitud: {}, nuevoLongitud: {}, nuevoFechaDeAcontecimiento: {}, nuevoContenidoMultimedia: {}",
        id, id, input.getNuevoEstadoSolicitud(), input.getNuevoTitulo(), input.getNuevoDescripcion(),
        input.getNuevoNombreCategoria(), input.getNuevoLatitud(), input.getNuevoLongitud(),
        input.getNuevoFechaDeAcontecimiento(), input.getNuevoContenidoMultimedia());
    SolicitudOutputDTO solicitudRevisada = solicitudesService.revisionSolicitud(id, input);
    return ResponseEntity.ok(solicitudRevisada);
  }
}

