package ar.utn.ba.ddsi.dinamica.controller;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudHechoInputContribuyenteDTO;
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
    log.info("GET /api/solicitudes/{}", id);
    return ResponseEntity.ok(solicitudesService.obtenerSolicitud(id));
  }

  @GetMapping("/contribuyente/{idContribuyente}")
  public ResponseEntity<List<SolicitudOutputDTO>> obtenerSolicitudesPorContribuyente(
      @PathVariable Long idContribuyente) {
    log.info("GET /api/solicitudes/contribuyente/{}", idContribuyente);
    return ResponseEntity.ok(solicitudesService.buscarPorContribuyente(idContribuyente));
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SolicitudOutputDTO> crearNuevaSolicitudJson(
      @Valid @RequestBody SolicitudHechoInputContribuyenteDTO input) {

    log.info("POST /api/solicitudes (JSON) - idContribuyente: {}, titulo: {}, descripcion: {}, categoria: {}, latitud: {}, longitud: {}, fechaDeAcontecimiento: {}, multimedia: {}",
            input.getIdContribuyente(), input.getTitulo(), input.getDescripcion(), input.getCategoria(),
            input.getLatitud(), input.getLongitud(), input.getFechaDeAcontecimiento(), input.getMultimedia());
    var solicitudCreada = solicitudesService.recibirSolicitudContribuyente(input);
    log.debug("POST /api/solicitudes (JSON) - Solicitud creada exitosamente con ID: {}", solicitudCreada.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(solicitudCreada.toOutputDTO());
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> crearNuevaSolicitudMultipart(
      @RequestPart("solicitud") String solicitudJson,
      @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {

    log.info("POST /api/solicitudes (MULTIPART) - solicitudJson: {}, archivos: {}",
            solicitudJson, archivos != null ? archivos.size() : 0);
    
    if (archivos != null && !archivos.isEmpty()) {
      archivos.forEach(archivo -> 
          log.info("POST /api/solicitudes (MULTIPART) - Archivo: {} ({} bytes, tipo: {})", 
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

  @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> modificarSolicitud(
      @PathVariable Long id,
      @RequestPart("solicitud") String solicitudJson,
      @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {
    
    log.info("PATCH /api/solicitudes/{} (MULTIPART) - solicitudJson: {}, archivos: {}",
            id, solicitudJson, archivos != null ? archivos.size() : 0);
    
    if (archivos != null && !archivos.isEmpty()) {
      archivos.forEach(archivo -> 
          log.info("PATCH /api/solicitudes/{} (MULTIPART) - Archivo: {} ({} bytes, tipo: {})", 
                  id, archivo.getOriginalFilename(), archivo.getSize(), archivo.getContentType()));
    }
    
    try {
      SolicitudOutputDTO solicitudModificada = solicitudesService.modificarSolicitudConMultipart(
          id, solicitudJson, archivos);
      log.debug("Solicitud {} modificada exitosamente", id);
      return ResponseEntity.ok(solicitudModificada);
    } catch (IllegalArgumentException e) {
      log.error("PATCH /api/solicitudes/{} - Formato de archivo no permitido: {}", id, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Formato de archivo no permitido: " + e.getMessage());
    } catch (IOException e) {
      log.error("PATCH /api/solicitudes/{} - Error procesando solicitud con archivos: {}", 
          id, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Error procesando archivos: " + e.getMessage());
    } catch (Exception e) {
      log.error("Error modificando solicitud {}: {}", id, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error al modificar la solicitud: " + e.getMessage());
    }
  }

  @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> modificarSolicitudJson(
      @PathVariable Long id,
      @Valid @RequestBody SolicitudModificarUserInputDTO input) {
    
    log.info("PATCH /api/solicitudes/{} (JSON) - idContribuyente: {}, nuevoTitulo: {}, nuevoDescripcion: {}, nuevoNombreCategoria: {}, nuevoLatitud: {}, nuevoLongitud: {}, nuevoFechaDeAcontecimiento: {}, nuevoContenidoMultimedia: {}",
            id, input.getIdContribuyente(), input.getNuevoTitulo(), input.getNuevoDescripcion(),
            input.getNuevoNombreCategoria(), input.getNuevoLatitud(), input.getNuevoLongitud(),
            input.getNuevoFechaDeAcontecimiento(), input.getNuevoContenidoMultimedia());
    
    try {
      SolicitudOutputDTO solicitudModificada = solicitudesService.modificarSolicitud(id, input);
      log.debug("Solicitud {} modificada exitosamente", id);
      return ResponseEntity.ok(solicitudModificada);
    } catch (Exception e) {
      log.error("Error modificando solicitud {}: {}", id, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error al modificar la solicitud: " + e.getMessage());
    }
  }
}

