package ar.utn.ba.ddsi.dinamica.controller;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudHechoInputContribuyenteDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarUserInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.dinamica.service.SolicitudesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "${frontend.url}")
@RequiredArgsConstructor
@Validated
@Slf4j
public class SolicitudesController {
  private final SolicitudesService solicitudesService;

  @GetMapping("/{id}")
  public ResponseEntity<SolicitudOutputDTO> obtenerSolicitudPorId(@PathVariable Long id) {
    log.debug("GET /api/solicitudes/{}", id);
    return ResponseEntity.ok(solicitudesService.obtenerSolicitud(id));
  }

  @PostMapping
  public ResponseEntity<SolicitudOutputDTO> crearNuevaSolicitud(
      @Valid @RequestBody SolicitudHechoInputContribuyenteDTO input) {

    log.info("POST /api/solicitudes - Nueva solicitud de contribuyente ID: {}", input.getIdContribuyente());
    var solicitudCreada = solicitudesService.recibirSolicitudContribuyente(input);
    log.info("Solicitud creada con ID: {}", solicitudCreada.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(solicitudCreada.toOutputDTO());
  }

  @PatchMapping("/{id}")
  public ResponseEntity<SolicitudOutputDTO> modificarSolicitud(
      @PathVariable Long id,
      @Valid @RequestBody SolicitudModificarUserInputDTO input) {
    log.info("PATCH /api/solicitudes/{} - Modificación por contribuyente ID: {}", id, input.getIdContribuyente());
    SolicitudOutputDTO solicitudModificada = solicitudesService.modificarSolicitud(id, input);
    log.info("Solicitud {} modificada exitosamente", id);
    return ResponseEntity.ok(solicitudModificada);
  }
}

