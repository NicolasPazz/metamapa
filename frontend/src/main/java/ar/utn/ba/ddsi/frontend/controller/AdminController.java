package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.dtos.*;
import ar.utn.ba.ddsi.frontend.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Comparator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final ColeccionService coleccionService;
    private final SolicitudService solicitudService;
    private final AdminService adminService;
    private final ObjectMapper objectMapper;
    private final FuenteService fuenteService;
    private final EstadisticasService estadisticasService;
    @Value("${app.api.dinamica.base-url:http://localhost:8081/api}")
    private String dinamicaBaseUrl;

    @GetMapping("/admin")
    public String mostrarMenu(Model model, HttpServletRequest request) {
        log.info("[AdminController] - GET - /admin");
        if (request != null) {
            model.addAttribute("httpServletRequest", request);
        }
        

       
        List<ColeccionDTO> colecciones = coleccionService.getAllColecciones();
        List<SolicitudDTO> solicitudes = adminService.getAllSolicitudes();
        List<SolicitudInputDTO> solicitudesCarga = solicitudService.buscarTodasLasSolicitudes();
        List<FuenteDTO> fuentes = fuenteService.getAllFuentes();
        List<String> provincias = coleccionService.getProvinciasConHechos();
        List<String> nombresFuentes = coleccionService.getFuentesConHechos();
        Map<Long, String> mapFuentes = fuentes.stream()
                .collect(Collectors.toMap(FuenteDTO::getId, FuenteDTO::getNombre));

        // Las estadísticas se cargarán de forma asíncrona en el frontend
        // para no bloquear la carga inicial de la página
        model.addAttribute("coleccionesProvincia", Collections.emptyList());
        model.addAttribute("categoriasProvincia", Collections.emptyList());
        model.addAttribute("categoriasHora", Collections.emptyList());
        model.addAttribute("categoriaDominante", null);
        model.addAttribute("spam", null);

        model.addAttribute("titulo", "Panel De Control");
        model.addAttribute("hechosLimit", Collections.emptyList());
        model.addAttribute("coleccionesLimit", Collections.emptyList());
        model.addAttribute("solicitudesLimit", Collections.emptyList());

        model.addAttribute("colecciones", colecciones);
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("solicitudesCarga", solicitudesCarga);
        model.addAttribute("fuentes", fuentes);
        model.addAttribute("provincias", provincias);
        model.addAttribute("nombresFuentes", nombresFuentes);
        model.addAttribute("mapFuentes", mapFuentes);
        model.addAttribute("objectMapper", objectMapper);
        model.addAttribute("dinamicaBaseUrl", dinamicaBaseUrl);
        return "admin/admin";
    }

    @PostMapping("/admin/actualizarSolicitud")
    public String actualizarSolicitud(@RequestParam("id") Long solicitudId,
                                      @RequestParam("estado") String estado,
                                      RedirectAttributes redirectAttributes) {
        log.info("[AdminController] - POST - /admin/actualizarSolicitud - id: {}, estado: {}", solicitudId, estado);
        try {
            adminService.actualizarSolicitud(solicitudId, estado);
            String mensaje = estado.equals("ACEPTADA") 
                ? "Solicitud de eliminación aprobada. El hecho ha sido ocultado." 
                : "Solicitud de eliminación rechazada.";
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (WebClientResponseException e) {
            String errorMsg = e.getResponseBodyAsString();
            log.error("[AdminController] - Error al actualizar solicitud: {} - {}", e.getStatusCode(), errorMsg, e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + errorMsg);
        } catch (Exception e) {
            log.error("[AdminController] - Error al actualizar solicitud: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/admin";
    }


    @PostMapping("/admin/subir-csv")
    public String procesarCsv(@RequestParam("archivo") MultipartFile archivo, Model model) {
        log.info("[AdminController] - POST - /admin/subir-csv - archivo: {} ({} bytes, tipo: {})",
                archivo.getOriginalFilename(), archivo.getSize(), archivo.getContentType());
        try {
            adminService.enviarArchivoAlBackend(archivo);
            model.addAttribute("mensaje", "Archivo enviado exitosamente para procesamiento.");
        } catch (Exception e) {
            model.addAttribute("mensaje", "Error: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/crear-coleccion")
    public String crearColeccion(@Valid @ModelAttribute CrearColeccionDTO coleccionDTO, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (coleccionDTO.getCriterios() == null) {
            coleccionDTO.setCriterios(new java.util.ArrayList<>());
        }
        
        log.info("[AdminController] - POST - /admin/crear-coleccion - titulo: {}, descripcion: {}, criterios: {} (size: {}), algoritmoConsenso: {}, fuenteId: {}",
                coleccionDTO.getTitulo(), coleccionDTO.getDescripcion(), coleccionDTO.getCriterios(),
                coleccionDTO.getCriterios().size(), coleccionDTO.getAlgoritmoConsenso(), coleccionDTO.getFuenteId());
        
        if(!coleccionDTO.isValid()) {
            log.warn("[AdminController] - Validación fallida: criterios es null o vacío");
            model.addAttribute("mensaje", "Error: faltan datos para la colección");
            return "redirect:/admin";
        }
        try {
            adminService.crearColeccion(coleccionDTO);
            redirectAttributes.addFlashAttribute("success", "Colección creada correctamente.");
        } catch (Exception e) {
            log.error("[AdminController] ERROR - Error al crear colección: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/actualizar-coleccion")
    public String actualizarColeccion(@Valid @ModelAttribute CrearColeccionDTO coleccionDTO, @RequestParam("handle") String handle, Model model, RedirectAttributes redirectAttributes) {
        log.info("[AdminController] - POST - /admin/actualizar-coleccion - handle: {}, titulo: {}, descripcion: {}, criterios: {}, algoritmoConsenso: {}, fuenteId: {}",
                handle, coleccionDTO.getTitulo(), coleccionDTO.getDescripcion(), coleccionDTO.getCriterios(),
                coleccionDTO.getAlgoritmoConsenso(), coleccionDTO.getFuenteId());
        if(!coleccionDTO.isValid()) {
            model.addAttribute("mensaje", "Error: faltan datos para la colección");
            return "redirect:/admin";
        }
        try {
            adminService.actualizarColeccion(coleccionDTO, handle);
            redirectAttributes.addFlashAttribute("success", "Colección actualizada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin";
    }
    @GetMapping("/admin/solicitudes")
    public String redirigirASolicitudes() {
        return "redirect:/solicitudes";
    }

    @PostMapping("/admin/agregar-etiquetas")
    public String agregarEtiquetas(@RequestParam("hechoId") Long hechoId,
                                   @RequestParam("etiquetas") List<String> etiquetas,
                                   RedirectAttributes redirectAttributes) {
        log.info("[AdminController] - POST - /admin/agregar-etiquetas - hechoId: {}, etiquetas: {}", hechoId, etiquetas);
        try {
            adminService.agregarEtiquetasAHecho(hechoId, etiquetas);
            redirectAttributes.addFlashAttribute("success", "Etiquetas agregadas correctamente!");
        } catch (Exception e) {
            log.error("Error al agregar etiquetas al hecho {}", hechoId, e);
            redirectAttributes.addFlashAttribute("error", "Error al agregar etiquetas: " + e.getMessage());
        }
        return "redirect:/admin";
    }
    @GetMapping("/solicitudes")
    public String verPanelSolicitudes(Model model, RedirectAttributes redirectAttributes) {
        log.info("[AdminController] - GET - /solicitudes");
        try {
            List<SolicitudInputDTO> solicitudes = solicitudService.buscarTodasLasSolicitudes();
            List<SolicitudInputDTO> solicitudesFiltradas = solicitudes.stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE)
                .collect(Collectors.toList());

            model.addAttribute("solicitudes", solicitudesFiltradas);
            model.addAttribute("isAdmin", true); // Marcar como admin para mostrar botones de acción
            model.addAttribute("dinamicaBaseUrl", dinamicaBaseUrl);
            log.info("[AdminController] - Se encontraron {} solicitudes pendientes (de {} totales)", 
                solicitudesFiltradas.size(), solicitudes.size());
        } catch (Exception e) {
            log.error("[AdminController] - Error al obtener solicitudes: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al cargar las solicitudes: " + e.getMessage());
            model.addAttribute("solicitudes", Collections.emptyList());
            model.addAttribute("isAdmin", true);
            model.addAttribute("dinamicaBaseUrl", dinamicaBaseUrl);
        }

        return "admin/solicitudes";
    }

    @PatchMapping("/solicitudes/{id}/aceptar")
    @ResponseBody
    public ResponseEntity<?> aceptarSolicitud(@PathVariable Long id) {
        log.info("[AdminController] - PATCH - /solicitudes/{}/aceptar", id);
        try {
            solicitudService.aceptarSolicitud(id);
            return ResponseEntity.ok("Solicitud aceptada correctamente");
        } catch (WebClientResponseException e) {
            String mensajeErrorDelBackend = e.getResponseBodyAsString();
            String mensaje = !mensajeErrorDelBackend.isEmpty()
                            ? mensajeErrorDelBackend 
                            : "Error al aceptar la solicitud. Por favor, intente nuevamente.";
            return ResponseEntity.status(e.getStatusCode()).body(mensaje);
        } catch (Exception e) {
            log.error("[AdminController] - Error al aceptar solicitud {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al aceptar la solicitud: " + e.getMessage());
        }
    }

    @PatchMapping("/solicitudes/{id}/rechazar")
    @ResponseBody
    public ResponseEntity<?> rechazarSolicitud(@PathVariable Long id) {
        log.info("[AdminController] - PATCH - /solicitudes/{}/rechazar", id);
        try {
            solicitudService.rechazarSolicitud(id);
            return ResponseEntity.ok("Solicitud rechazada correctamente");
        } catch (WebClientResponseException e) {
            String mensajeErrorDelBackend = e.getResponseBodyAsString();
            String mensaje = !mensajeErrorDelBackend.isEmpty()
                            ? mensajeErrorDelBackend 
                            : "Error al rechazar la solicitud. Por favor, intente nuevamente.";
            return ResponseEntity.status(e.getStatusCode()).body(mensaje);
        } catch (Exception e) {
            log.error("[AdminController] - Error al rechazar solicitud {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al rechazar la solicitud: " + e.getMessage());
        }
    }

    @GetMapping("/solicitudes/{id}/aceptar-con-modificaciones")
    public String mostrarFormularioAceptarConModificaciones(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.info("[AdminController] - GET - /solicitudes/{}/aceptar-con-modificaciones", id);
        try {
            SolicitudInputDTO solicitud = solicitudService.obtenerSolicitudPorId(id);
            if (solicitud == null) {
                redirectAttributes.addFlashAttribute("error", "Solicitud no encontrada.");
                return "redirect:/solicitudes";
            }
            model.addAttribute("solicitud", solicitud);
            model.addAttribute("esAceptarConModificaciones", true);
            model.addAttribute("dinamicaBaseUrl", dinamicaBaseUrl);
            return "admin/editar_solicitud";
        } catch (Exception e) {
            log.error("[AdminController] - Error al obtener solicitud {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al cargar la solicitud: " + e.getMessage());
            return "redirect:/solicitudes";
        }
    }

    @PatchMapping(value = "/solicitudes/{id}/aceptar-con-modificaciones", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<?> aceptarSolicitudConModificaciones(
            @PathVariable Long id,
            @ModelAttribute SolicitudEdicionFormDTO formDTO) {
        log.info("[AdminController] - PATCH - /solicitudes/{}/aceptar-con-modificaciones - id: {}, titulo: {}, descripcion: {}, categoria: {}, latitud: {}, longitud: {}, fecha_acontecimiento: {}, archivos: {}",
                id, id, formDTO.getTitulo(), formDTO.getDescripcion(),
                formDTO.getCategoria(), formDTO.getLatitud(), formDTO.getLongitud(),
                formDTO.getFecha_acontecimiento(), formDTO.getArchivos() != null ? formDTO.getArchivos().size() : 0);
        try {
            solicitudService.aceptarSolicitudConModificaciones(id, formDTO);
            return ResponseEntity.ok("Solicitud aceptada con modificaciones correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (WebClientResponseException e) {
            String mensajeErrorDelBackend = e.getResponseBodyAsString();
            String mensaje = !mensajeErrorDelBackend.isEmpty()
                            ? mensajeErrorDelBackend 
                            : "Error al aceptar la solicitud con modificaciones. Por favor, intente nuevamente.";
            return ResponseEntity.status(e.getStatusCode()).body(mensaje);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al aceptar la solicitud con modificaciones: " + e.getMessage());
        }
    }

    @PostMapping("/admin/actualizar-agregador")
    public String actualizarAgregador(RedirectAttributes redirectAttributes) {
      log.info("[AdminController] - POST - /admin/actualizar-agregador");
      try {
        adminService.ejecutarSchedulerAgregador();
        redirectAttributes.addFlashAttribute("success", "Agregador actualizado correctamente!");
      } catch (Exception e) {
        log.error("Error al ejecutar scheduler del agregador", e);
        redirectAttributes.addFlashAttribute("error", "Error al actualizar agregador: " + e.getMessage());
      }
      return "redirect:/admin";
    }

    @PostMapping("/admin/actualizar-estadisticas")
    public String actualizarEstadisticas(RedirectAttributes redirectAttributes) {
      log.info("[AdminController] - POST - /admin/actualizar-estadisticas");
      try {
        adminService.actualizarEstadisticas();
        redirectAttributes.addFlashAttribute("success", "Estadísticas actualizadas correctamente!");
      } catch (Exception e) {
        log.error("Error al actualizar estadísticas", e);
        redirectAttributes.addFlashAttribute("error", "Error al actualizar estadísticas: " + e.getMessage());
      }
      return "redirect:/admin";
    }

    @GetMapping("/admin/api/estadisticas")
    @ResponseBody
    public EstadisticasCompletasDTO obtenerEstadisticasApi() {
        log.info("[AdminController] - GET - /admin/api/estadisticas");
        try {
            EstadisticasCompletasDTO estadisticas = estadisticasService.obtenerEstadisticas();
            if (estadisticas.getDistribucionGeograficaColecciones() != null) {
                estadisticas.getDistribucionGeograficaColecciones()
                    .sort(Comparator.comparing(ColeccionProvinciaDTO::getColeccionHandle));
            }
            if (estadisticas.getConcentracionGeograficaCategorias() != null) {
                estadisticas.getConcentracionGeograficaCategorias()
                    .sort(Comparator.comparing(CategoriaProvinciaDTO::getProvinciaNombre));
            }
            if (estadisticas.getDistribucionTemporalCategorias() != null) {
                estadisticas.getDistribucionTemporalCategorias()
                    .sort(Comparator.comparing(CategoriaHoraDTO::getCategoriaNombre));
            }
            return estadisticas;
        } catch (Exception e) {
            log.warn("Error al obtener estadísticas para el panel admin: {}", e.getMessage());
            return EstadisticasCompletasDTO.builder()
                .distribucionGeograficaColecciones(Collections.emptyList())
                .concentracionGeograficaCategorias(Collections.emptyList())
                .distribucionTemporalCategorias(Collections.emptyList())
                .categoriaDominante(null)
                .estadisticaSpam(null)
                .build();
        }
    }
}

