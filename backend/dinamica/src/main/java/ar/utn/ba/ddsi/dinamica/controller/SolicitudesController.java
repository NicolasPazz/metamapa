package ar.utn.ba.ddsi.dinamica.controller;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudHechoInputContribuyenteDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarAdminInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarUserInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;
import ar.utn.ba.ddsi.dinamica.service.SolicitudesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import static ar.utn.ba.ddsi.dinamica.domain.solicitud.EstadoSolicitud.*;


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

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SolicitudOutputDTO> crearNuevaSolicitudJson(
          @Valid @RequestBody SolicitudHechoInputContribuyenteDTO input) {

    log.debug("POST /api/solicitudes (JSON) - Recibida petición");
    log.debug("POST /api/solicitudes (JSON) - Nueva solicitud de contribuyente ID: {}", input.getIdContribuyente());
    log.debug("POST /api/solicitudes (JSON) - Título: {}, Latitud: {}, Longitud: {}",
            input.getTitulo(), input.getLatitud(), input.getLongitud());
    var solicitudCreada = solicitudesService.recibirSolicitudContribuyente(input);
    log.debug("POST /api/solicitudes (JSON) - Solicitud creada exitosamente con ID: {}", solicitudCreada.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(solicitudCreada.toOutputDTO());
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> crearNuevaSolicitudMultipart(
          @RequestPart("solicitud") String solicitudJson,
          @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {

    log.debug("POST /api/solicitudes (MULTIPART) - Recibida petición");
    log.debug("POST /api/solicitudes - JSON recibido: {}", solicitudJson);
    log.debug("POST /api/solicitudes - Archivos recibidos: {}",
            archivos != null ? archivos.size() : 0);

    if (archivos != null) {
      archivos.forEach(archivo ->
              log.debug("POST /api/solicitudes - Archivo: {} ({} bytes, tipo: {})",
                      archivo.getOriginalFilename(), archivo.getSize(), archivo.getContentType()));
    }

    try {
      SolicitudHecho solicitudCreada = solicitudesService.recibirSolicitudContribuyenteConMultipart(
              solicitudJson, archivos);
      log.debug("POST /api/solicitudes - Solicitud creada exitosamente con ID: {}",
              solicitudCreada.getId());
      return ResponseEntity.status(HttpStatus.CREATED).body(solicitudCreada.toOutputDTO());
    } catch (IllegalArgumentException e) {
      log.error("POST /api/solicitudes - Formato de archivo no permitido: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body("Formato de archivo no permitido: " + e.getMessage());
    } catch (IOException e) {
      log.error("POST /api/solicitudes - Error procesando solicitud con archivos: {}",
              e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body("Error procesando archivos: " + e.getMessage());
    } catch (Exception e) {
      log.error("POST /api/solicitudes - Error inesperado procesando solicitud: {}",
              e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body("Error inesperado: " + e.getMessage());
    }
  }

  @PatchMapping("/{id}")
  public ResponseEntity<SolicitudOutputDTO> modificarSolicitud(
          @PathVariable Long id,
          @Valid @RequestBody SolicitudModificarUserInputDTO input) {
    log.debug("PATCH /api/solicitudes/{} - Modificación por contribuyente ID: {}", id, input.getIdContribuyente());
    SolicitudOutputDTO solicitudModificada = solicitudesService.modificarSolicitud(id, input);
    log.debug("Solicitud {} modificada exitosamente", id);
    return ResponseEntity.ok(solicitudModificada);
  }

  @PatchMapping("/{id}/aceptar")
  public ResponseEntity<SolicitudOutputDTO> aceptarSolicitud(@PathVariable Long id) {
    SolicitudModificarAdminInputDTO solicitudCambio = new SolicitudModificarAdminInputDTO();
    solicitudCambio.setNuevoEstadoSolicitud(ACEPTADO);

    SolicitudOutputDTO resultado = solicitudesService.revisionSolicitud(id, solicitudCambio);
    return ResponseEntity.ok(resultado);
  }

  @PatchMapping("/{id}/rechazar")
  public ResponseEntity<SolicitudOutputDTO> rechazarSolicitud(@PathVariable Long id) {
    SolicitudModificarAdminInputDTO solicitudCambio = new SolicitudModificarAdminInputDTO();
    solicitudCambio.setNuevoEstadoSolicitud(RECHAZADO);

    SolicitudOutputDTO resultado = solicitudesService.revisionSolicitud(id, solicitudCambio);
    return ResponseEntity.ok(resultado);

  }
}

