package ar.utn.ba.ddsi.dinamica.controller;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarAdminInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarUserInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudHechoInputContribuyenteDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.dinamica.service.ISolicitudesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@RestController
@RequestMapping("api/solicitudes")
@CrossOrigin(origins = "http://localhost:3000")

public class SolicitudesController {

  @Autowired
  private ISolicitudesService solicitudesService;


//TODO: validar que solo pueda hacerlo los admins
//--------------------MOSTRAR TODAS LAS SOLICITUDES---------------------
  @GetMapping
  public ResponseEntity<List<SolicitudOutputDTO>> buscarSolicitudes() {
    List<SolicitudOutputDTO> solicitudes = solicitudesService.buscarTodas();
    return ResponseEntity.ok(solicitudes);
  }

  //TODO: queda pendiente validar que solo lo pueda ver el user o el admin (pediria un id mas y valido si existe en mi base de datos user o admin)
  //-----------------TANTO USER COMO ADMIN PUEDEN VISUALIZAR SOLICITUDES ESPECIFICAS-------------------------
  @GetMapping("/{id}")
  public ResponseEntity<SolicitudOutputDTO> obtenerSolicitud(@PathVariable Long id){
     SolicitudOutputDTO solicitud = solicitudesService.obtenerSolicitud(id);
     if (solicitud != null){
       return ResponseEntity.ok(solicitud);
     }else{
       return ResponseEntity.notFound().build();
     }
  }


  //-------------------------------USUARIO CARGA SOLICITUD--------------------------------------
  @PostMapping
  public ResponseEntity<String> recibirSolicitud(@RequestBody SolicitudHechoInputContribuyenteDTO input){
    try {
      solicitudesService.recibirSolicitudContribuyente(input);
      return ResponseEntity.status(HttpStatus.CREATED).body("Solicitud recibida correctamente.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la solicitud.");
    }
  }



//-------------------------------CAMBIOS A SOLICITUDES--------------------------------------------------------

  @PatchMapping("/user/{id}")
  public ResponseEntity<SolicitudOutputDTO> modificarUserSolicitud(@PathVariable Long id, @RequestBody SolicitudModificarUserInputDTO input) {
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
  public ResponseEntity<SolicitudOutputDTO> modificarAdminSolicitud(@PathVariable Long id, @RequestBody SolicitudModificarAdminInputDTO input) {
    try {
      SolicitudOutputDTO modificado = this.solicitudesService.revisionSolicitud(id, input);
      return ResponseEntity.ok(modificado);
    } catch (ResponseStatusException ex) {
      return ResponseEntity.status(ex.getStatusCode()).body(null);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

}
