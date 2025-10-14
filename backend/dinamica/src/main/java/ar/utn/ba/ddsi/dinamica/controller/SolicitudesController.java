package ar.utn.ba.ddsi.dinamica.controller;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudHechoInputContribuyenteDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarAdminInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarUserInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.dinamica.service.ISolicitudesService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "${frontend.url}")
@RequiredArgsConstructor
public class SolicitudesController {

  private final ISolicitudesService solicitudesService;

//--------------------MOSTRAR TODAS LAS SOLICITUDES---------------------
  @GetMapping
  public ResponseEntity<List<SolicitudOutputDTO>> buscarSolicitudes() {
    List<SolicitudOutputDTO> solicitudes = solicitudesService.buscarTodas();
    return ResponseEntity.ok(solicitudes);
  }

  //-----------------TANTO USER COMO ADMIN PUEDEN VISUALIZAR SOLICITUDES ESPECIFICAS-------------------------
  @GetMapping("/{id}")
  public ResponseEntity<SolicitudOutputDTO> obtenerSolicitud(@PathVariable Long id) {
    SolicitudOutputDTO solicitud = solicitudesService.obtenerSolicitud(id);
    if (solicitud != null) {
      return ResponseEntity.ok(solicitud);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  //-------------------------------USUARIO CARGA SOLICITUD--------------------------------------
  // Crea un nuevo hecho aportado por un contribuyente (anónimo o registrado). Permite adjuntar título, descripción,
  // categoría, ubicación, fecha y multimedia.
  @PostMapping
  public ResponseEntity<String> recibirSolicitud(@RequestBody SolicitudHechoInputContribuyenteDTO input) {
    try {
      solicitudesService.recibirSolicitudContribuyente(input);
      return ResponseEntity.status(HttpStatus.CREATED).body("Solicitud recibida correctamente.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud.");
    }
  }

//-------------------------------CAMBIOS A SOLICITUDES--------------------------------------------------------
  @PatchMapping("/user/{id}")
  // Edita un hecho existente que fue creado por el contribuyente registrado. Solo permitido dentro de un plazo de 7 días.
  public ResponseEntity<SolicitudOutputDTO> modificarUserSolicitud(@PathVariable Long id,
                                                                   @RequestBody SolicitudModificarUserInputDTO input) {
    try {
      SolicitudOutputDTO modificado = this.solicitudesService.modificarSolicitud(id, input);
      return ResponseEntity.ok(modificado);
    } catch (ResponseStatusException ex) {
      return ResponseEntity.status(ex.getStatusCode()).body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @PatchMapping("/admin/{id}")
  // El administrador debe poder aprobar, rechazar o aceptar con modificaciones los hechos subidos por contribuyentes.
  public ResponseEntity<SolicitudOutputDTO> modificarAdminSolicitud(@PathVariable Long id,
                                                                    @RequestBody SolicitudModificarAdminInputDTO input) {
      SolicitudOutputDTO modificado = this.solicitudesService.revisionSolicitud(id, input);
      return ResponseEntity.ok(modificado);

  }
}