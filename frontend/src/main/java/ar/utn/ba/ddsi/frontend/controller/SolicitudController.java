package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudEdicionFormDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudInputDTO;
import ar.utn.ba.ddsi.frontend.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
/*
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
*/


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
    @GetMapping()
    public String verPanelSolicitudes(Model model) {
        // Verificar si el usuario es admin
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_ADMIN"));
        
        List<SolicitudInputDTO> solicitudes;
        if (isAdmin) {
            // Si es admin, mostrar todas las solicitudes
            solicitudes = solicitudService.buscarTodasLasSolicitudes();
        } else {
            // Si no es admin, mostrar solo las solicitudes del usuario
            Long userId = solicitudService.obtenerContribuyenteIdDeSesion();
            if (userId != null) {
                solicitudes = solicitudService.obtenerSolicitudesPorContribuyente(userId);
            } else {
                solicitudes = List.of(); // Si no hay userId, lista vacía
            }
        }
        
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("isAdmin", isAdmin);
        
        return "admin/solicitudes";
    }
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        log.info("Intento de acceder a edición de solicitud ID: {}", id);
        try {
            SolicitudInputDTO solicitud = solicitudService.buscarSolicitudPorId(id);

            if (solicitud == null) {
                log.warn("La solicitud ID {} retornó NULL desde el servicio.", id);
                return "redirect:/admin/solicitudes?error=NoEncontrado";
            }

            log.info("Solicitud encontrada: {}", solicitud); // Asegúrate que tu DTO tenga toString()
            model.addAttribute("solicitud", solicitud);
            return "admin/editar_solicitud";

        } catch (Exception e) {
            log.error("Error grave al intentar buscar la solicitud ID: " + id, e);
            return "redirect:/admin/solicitudes?error=ErrorInterno";
        }
    }


    @PostMapping("/{id}/editar/")
    public String procesarEdicion(@PathVariable Long id, @ModelAttribute SolicitudEdicionFormDTO solicitudEditada) {
        solicitudService.procesarSolicitudEdicionConMultipart(id, solicitudEditada);

        // Redirigimos al listado principal
        return "redirect:/admin/solicitudes";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}/aceptar-con-modificaciones")
    public String mostrarFormularioAceptarConModificaciones(@PathVariable Long id, Model model) {
        log.info("Intento de acceder a aceptar con modificaciones de solicitud ID: {}", id);
        try {
            SolicitudInputDTO solicitud = solicitudService.buscarSolicitudPorId(id);

            if (solicitud == null) {
                log.warn("La solicitud ID {} retornó NULL desde el servicio.", id);
                return "redirect:/admin/solicitudes?error=NoEncontrado";
            }

            log.info("Solicitud encontrada para aceptar con modificaciones: {}", solicitud);
            model.addAttribute("solicitud", solicitud);
            model.addAttribute("esAceptarConModificaciones", true);
            return "admin/editar_solicitud";
        } catch (Exception e) {
            log.error("Error grave al intentar buscar la solicitud ID: " + id, e);
            return "redirect:/admin/solicitudes?error=ErrorInterno";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping(value = "/{id}/aceptar-con-modificaciones", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<?> procesarAceptarConModificaciones(
            @PathVariable Long id,
            @ModelAttribute SolicitudEdicionFormDTO formDTO) {
        log.info("PATCH /solicitudes/{}/aceptar-con-modificaciones - contribuyente_id: {}, titulo: {}, descripcion: {}, categoria: {}, latitud: {}, longitud: {}, fecha_acontecimiento: {}, archivos: {}",
                id, formDTO.getContribuyente_id(), formDTO.getTitulo(), formDTO.getDescripcion(),
                formDTO.getCategoria(), formDTO.getLatitud(), formDTO.getLongitud(),
                formDTO.getFecha_acontecimiento(), formDTO.getArchivos() != null ? formDTO.getArchivos().size() : 0);
        try {
            solicitudService.procesarAceptarConModificaciones(id, formDTO);
            log.debug("Solicitud {} aceptada con modificaciones correctamente", id);
            return ResponseEntity.ok("Solicitud aceptada con modificaciones correctamente");
        } catch (MaxUploadSizeExceededException e) {
            log.warn("Archivo demasiado grande al aceptar solicitud con modificaciones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body("El tamaño del archivo excede el límite permitido de 50 MB. " +
                          "Por favor, comprima o reduzca el tamaño de los archivos multimedia e intente nuevamente.");
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al aceptar solicitud con modificaciones {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (WebClientResponseException e) {
            String mensajeErrorDelBackend = e.getResponseBodyAsString();
            log.error("Error del backend al aceptar solicitud con modificaciones: {}", mensajeErrorDelBackend);
            String mensaje = !mensajeErrorDelBackend.isEmpty()
                            ? mensajeErrorDelBackend 
                            : "Error al aceptar la solicitud con modificaciones. Por favor, intente nuevamente.";
            return ResponseEntity.status(e.getStatusCode()).body(mensaje);
        } catch (Exception e) {
            log.error("Error al aceptar la solicitud {} con modificaciones: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Error al aceptar la solicitud con modificaciones: " + e.getMessage());
        }
    }

    
}



