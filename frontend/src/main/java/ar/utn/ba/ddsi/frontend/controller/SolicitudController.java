package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudEdicionFormDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudModificacionDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudInputDTO;
import ar.utn.ba.ddsi.frontend.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/solicitudes")
@Slf4j
public class SolicitudController {
    private final SolicitudService solicitudService;
    public String crearSolicitud(@ModelAttribute("solicitud") SolicitudDTO solicitudDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            //solicitudService.crearSolicitud(solicitudDTO);
            return "redirect:/hechos";
        } catch (Exception e) {
            return "redirect:/hechos";
        }


    }

    @PostMapping("/modificar/{idSolicitud}")
    public ResponseEntity<?> solicitarModificacion(
            @PathVariable Long idSolicitud  ,
            @RequestBody @Valid SolicitudModificacionDTO dto) {
        log.info("POST /solicitudes/modificar/{} - idContribuyente: {}, nuevoTitulo: {}, nuevoDescripcion: {}, nuevoNombreCategoria: {}, nuevoLatitud: {}, nuevoLongitud: {}, nuevoFechaDeAcontecimiento: {}, nuevoContenidoMultimedia: {}",
                idSolicitud, dto.getIdContribuyente(), dto.getNuevoTitulo(), dto.getNuevoDescripcion(),
                dto.getNuevoNombreCategoria(), dto.getNuevoLatitud(), dto.getNuevoLongitud(),
                dto.getNuevoFechaDeAcontecimiento(), dto.getNuevoContenidoMultimedia());

        return ResponseEntity.ok("Solicitud recibida correctamente");
    }

    @PatchMapping(value = "/{id}", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<?> editarSolicitud(
            @PathVariable Long id,
            @ModelAttribute SolicitudEdicionFormDTO formDTO) {
        log.info("PATCH /solicitudes/{} - contribuyente_id: {}, titulo: {}, descripcion: {}, categoria: {}, latitud: {}, longitud: {}, fecha_acontecimiento: {}, archivos: {}",
                id, formDTO.getContribuyente_id(), formDTO.getTitulo(), formDTO.getDescripcion(),
                formDTO.getCategoria(), formDTO.getLatitud(), formDTO.getLongitud(),
                formDTO.getFecha_acontecimiento(), formDTO.getArchivos() != null ? formDTO.getArchivos().size() : 0);
        try {
            solicitudService.procesarSolicitudEdicionConMultipart(id, formDTO);
            log.debug("Solicitud {} actualizada correctamente", id);
            return ResponseEntity.ok("Solicitud actualizada correctamente");
        } catch (MaxUploadSizeExceededException e) {
            log.warn("Archivo demasiado grande al editar solicitud: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body("El tamaño del archivo excede el límite permitido de 50 MB. " +
                          "Por favor, comprima o reduzca el tamaño de los archivos multimedia e intente nuevamente.");
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al actualizar solicitud {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (WebClientResponseException e) {
            String mensajeErrorDelBackend = e.getResponseBodyAsString();
            log.error("Error del backend al editar solicitud: {}", mensajeErrorDelBackend);
            String mensaje = !mensajeErrorDelBackend.isEmpty()
                            ? mensajeErrorDelBackend 
                            : "Error al editar la solicitud. Por favor, intente nuevamente.";
            return ResponseEntity.status(e.getStatusCode()).body(mensaje);
        } catch (Exception e) {
            log.error("Error al actualizar la solicitud {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Error al actualizar la solicitud: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/aceptar")
    public String aceptarSolicitud(@PathVariable Long id) {
        solicitudService.cambiarEstado(id, "ACEPTADO");
        return "redirect:/admin/solicitudes";

    }
    @PostMapping("/{id}/rechazar")
    public String rechazarSolicitud(@PathVariable Long id) {
        solicitudService.cambiarEstado(id, "RECHAZADO");

        return "redirect:/admin/solicitudes";
    }
    
}



