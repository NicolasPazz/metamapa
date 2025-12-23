package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudEdicionFormDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudModificacionDTO;
import ar.utn.ba.ddsi.frontend.service.SolicitudService;
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
        log.info("[SolicitudController] - POST - /solicitudes/modificar/{} - idSolicitud: {}, idContribuyente: {}, nuevoTitulo: {}, nuevoDescripcion: {}, nuevoNombreCategoria: {}, nuevoLatitud: {}, nuevoLongitud: {}, nuevoFechaDeAcontecimiento: {}, nuevoContenidoMultimedia: {}",
                idSolicitud, idSolicitud, dto.getIdContribuyente(), dto.getNuevoTitulo(), dto.getNuevoDescripcion(),
                dto.getNuevoNombreCategoria(), dto.getNuevoLatitud(), dto.getNuevoLongitud(),
                dto.getNuevoFechaDeAcontecimiento(), dto.getNuevoContenidoMultimedia());
        return ResponseEntity.ok("Solicitud recibida correctamente");
    }

    @PatchMapping(value = "/{id}", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<?> editarSolicitud(
            @PathVariable Long id,
            @ModelAttribute SolicitudEdicionFormDTO formDTO) {
        log.info("[SolicitudController] - PATCH - /solicitudes/{} - id: {}, contribuyente_id: {}, titulo: {}, descripcion: {}, categoria: {}, latitud: {}, longitud: {}, fecha_acontecimiento: {}, archivos: {}",
                id, id, formDTO.getContribuyente_id(), formDTO.getTitulo(), formDTO.getDescripcion(),
                formDTO.getCategoria(), formDTO.getLatitud(), formDTO.getLongitud(),
                formDTO.getFecha_acontecimiento(), formDTO.getArchivos() != null ? formDTO.getArchivos().size() : 0);
        try {
            solicitudService.procesarSolicitudEdicionConMultipart(id, formDTO);
            return ResponseEntity.ok("Solicitud actualizada correctamente");
        } catch (MaxUploadSizeExceededException e) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body("El tamaño del archivo excede el límite permitido de 50 MB. " +
                          "Por favor, comprima o reduzca el tamaño de los archivos multimedia e intente nuevamente.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (WebClientResponseException e) {
            String mensajeErrorDelBackend = e.getResponseBodyAsString();
            String mensaje = !mensajeErrorDelBackend.isEmpty()
                            ? mensajeErrorDelBackend 
                            : "Error al editar la solicitud. Por favor, intente nuevamente.";
            return ResponseEntity.status(e.getStatusCode()).body(mensaje);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar la solicitud: " + e.getMessage());
        }
    }
}



