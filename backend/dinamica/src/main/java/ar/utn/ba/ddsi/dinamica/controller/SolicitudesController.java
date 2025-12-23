package ar.utn.ba.ddsi.dinamica.controller;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudHechoInputContribuyenteDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarUserInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;
import ar.utn.ba.ddsi.dinamica.service.SolicitudesService;
import jakarta.persistence.EntityNotFoundException;
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
    log.info("[SolicitudesController] - GET - /api/solicitudes/{} - id: {}", id, id);
    try {
      return ResponseEntity.ok(solicitudesService.obtenerSolicitud(id));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  @GetMapping("/contribuyente/{idContribuyente}")
  public ResponseEntity<List<SolicitudOutputDTO>> obtenerSolicitudesPorContribuyente(
      @PathVariable Long idContribuyente) {
    log.info("[SolicitudesController] - GET - /api/solicitudes/contribuyente/{} - idContribuyente: {}", idContribuyente, idContribuyente);
    return ResponseEntity.ok(solicitudesService.buscarPorContribuyente(idContribuyente));
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SolicitudOutputDTO> crearNuevaSolicitudJson(
      @Valid @RequestBody SolicitudHechoInputContribuyenteDTO input) {
    log.info("[SolicitudesController] - POST - /api/solicitudes - idContribuyente: {}, titulo: {}, descripcion: {}, categoria: {}, latitud: {}, longitud: {}, fechaDeAcontecimiento: {}, multimedia: {}",
            input.getIdContribuyente(), input.getTitulo(), input.getDescripcion(), input.getCategoria(),
            input.getLatitud(), input.getLongitud(), input.getFechaDeAcontecimiento(), input.getMultimedia());
    var solicitudCreada = solicitudesService.recibirSolicitudContribuyente(input);
    return ResponseEntity.status(HttpStatus.CREATED).body(solicitudCreada.toOutputDTO());
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> crearNuevaSolicitudMultipart(
      @RequestPart("solicitud") String solicitudJson,
      @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {
    log.info("[SolicitudesController] - POST - /api/solicitudes - solicitudJson: {}, archivos: {}",
            solicitudJson, archivos != null ? archivos.size() : 0);
    
    if (archivos != null && !archivos.isEmpty()) {
      long archivosNoVacios = archivos.stream().filter(archivo -> !archivo.isEmpty()).count();
      if (archivosNoVacios > 5) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Solo se pueden cargar hasta 5 archivos. Se recibieron " + archivosNoVacios + " archivos.");
      }
    }

    try {
      SolicitudHecho solicitudCreada = solicitudesService.recibirSolicitudContribuyenteConMultipart(
          solicitudJson, archivos);
      return ResponseEntity.status(HttpStatus.CREATED).body(solicitudCreada.toOutputDTO());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Formato de archivo no permitido: " + e.getMessage());
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Error procesando archivos: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error inesperado: " + e.getMessage());
    }
  }

  @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> modificarSolicitud(
      @PathVariable Long id,
      @RequestPart("solicitud") String solicitudJson,
      @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {
    log.info("[SolicitudesController] - PATCH - /api/solicitudes/{} - id: {}, solicitudJson: {}, archivos: {}",
            id, id, solicitudJson, archivos != null ? archivos.size() : 0);
    
    if (archivos != null && !archivos.isEmpty()) {
      long archivosNoVacios = archivos.stream().filter(archivo -> !archivo.isEmpty()).count();
      if (archivosNoVacios > 5) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Solo se pueden cargar hasta 5 archivos. Se recibieron " + archivosNoVacios + " archivos.");
      }
    }
    
    try {
      SolicitudOutputDTO solicitudModificada = solicitudesService.modificarSolicitudConMultipart(
          id, solicitudJson, archivos);
      return ResponseEntity.ok(solicitudModificada);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Formato de archivo no permitido: " + e.getMessage());
    } catch (IOException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Error procesando archivos: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error al modificar la solicitud: " + e.getMessage());
    }
  }

  @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> modificarSolicitudJson(
      @PathVariable Long id,
      @Valid @RequestBody SolicitudModificarUserInputDTO input) {
    log.info("[SolicitudesController] - PATCH - /api/solicitudes/{} - id: {}, idContribuyente: {}, nuevoTitulo: {}, nuevoDescripcion: {}, nuevoNombreCategoria: {}, nuevoLatitud: {}, nuevoLongitud: {}, nuevoFechaDeAcontecimiento: {}, nuevoContenidoMultimedia: {}",
            id, id, input.getIdContribuyente(), input.getNuevoTitulo(), input.getNuevoDescripcion(),
            input.getNuevoNombreCategoria(), input.getNuevoLatitud(), input.getNuevoLongitud(),
            input.getNuevoFechaDeAcontecimiento(), input.getNuevoContenidoMultimedia());
    
    try {
      SolicitudOutputDTO solicitudModificada = solicitudesService.modificarSolicitud(id, input);
      return ResponseEntity.ok(solicitudModificada);
    } catch (EntityNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Error: " + e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error al modificar la solicitud: " + e.getMessage());
    }
  }
}

