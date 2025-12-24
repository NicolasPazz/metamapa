package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.dtos.*;
import ar.utn.ba.ddsi.frontend.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
    private final HechoService hechoService;
    private final SolicitudService solicitudService;
    private final AdminService adminService;
    private final ObjectMapper objectMapper;
    private final FuenteService fuenteService;

    @GetMapping("/admin")
    public String mostrarMenu(Model model, HttpServletRequest request) {
        log.info("[AdminController] - GET - /admin");
        if (request != null) {
            model.addAttribute("httpServletRequest", request);
        }
        List<ColeccionDTO> colecciones = coleccionService.getAllColecciones();
        List<HechoDTO> hechos = hechoService.getAllHechos();
        List<SolicitudDTO> solicitudes = adminService.getAllSolicitudes();
        List<FuenteDTO> fuentes = fuenteService.getAllFuentes();
        Map<Long, String> mapFuentes = fuentes.stream()
                .collect(Collectors.toMap(FuenteDTO::getId, FuenteDTO::getNombre));

        model.addAttribute("titulo", "Panel De Control");
        if (hechos != null && hechos.size() > 3) {
            int endIndex = Math.min(5, hechos.size());
            model.addAttribute("hechosLimit", hechos.subList(3, endIndex));
        } else {
            model.addAttribute("hechosLimit", Collections.emptyList());
        }
        model.addAttribute("colecciones", colecciones);
        model.addAttribute("hechos", hechos);
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("fuentes", fuentes);
        model.addAttribute("mapFuentes", mapFuentes);
        model.addAttribute("objectMapper", objectMapper);
        return "admin/admin";
    }

    @PostMapping("/admin/actualizarSolicitud")
    public String actualizarSolicitud(@RequestParam("id") Long solicitudId,
                                      @RequestParam("estado") String estado,
                                      RedirectAttributes redirectAttributes) {
        log.info("[AdminController] - POST - /admin/actualizarSolicitud - id: {}, estado: {}", solicitudId, estado);
        try {
            adminService.actualizarSolicitud(solicitudId, estado);
            redirectAttributes.addFlashAttribute("success", "Solicitud actualizada!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar.");
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
    public String crearColeccion(@Valid @ModelAttribute CrearColeccionDTO coleccionDTO, Model model) {
        log.info("[AdminController] - POST - /admin/crear-coleccion - titulo: {}, descripcion: {}, criterios: {}, algoritmoConsenso: {}, fuenteId: {}",
                coleccionDTO.getTitulo(), coleccionDTO.getDescripcion(), coleccionDTO.getCriterios(),
                coleccionDTO.getAlgoritmoConsenso(), coleccionDTO.getFuenteId());
        if(!coleccionDTO.isValid()) {
            model.addAttribute("mensaje", "Error: faltan datos para la colección");
            return "redirect:/admin";
        }
        try {
            adminService.crearColeccion(coleccionDTO);
            model.addAttribute("mensaje", "Colección creada correctamente.");
        } catch (Exception e) {
            model.addAttribute("mensaje", "Error: " + e.getMessage());
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/actualizar-coleccion")
    public String actualizarColeccion(@Valid @ModelAttribute CrearColeccionDTO coleccionDTO, @RequestParam("handle") String handle, Model model) {
        log.info("[AdminController] - POST - /admin/actualizar-coleccion - handle: {}, titulo: {}, descripcion: {}, criterios: {}, algoritmoConsenso: {}, fuenteId: {}",
                handle, coleccionDTO.getTitulo(), coleccionDTO.getDescripcion(), coleccionDTO.getCriterios(),
                coleccionDTO.getAlgoritmoConsenso(), coleccionDTO.getFuenteId());
        if(!coleccionDTO.isValid()) {
            model.addAttribute("mensaje", "Error: faltan datos para la colección");
            return "redirect:/admin";
        }
        try {
            adminService.actualizarColeccion(coleccionDTO, handle);
            model.addAttribute("mensaje", "Colección actualizada correctamente.");
        } catch (Exception e) {
            model.addAttribute("mensaje", "Error: " + e.getMessage());
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
    public String verPanelSolicitudes(Model model) {
        log.info("[AdminController] - GET - /solicitudes");
        List<SolicitudInputDTO> solicitudes = solicitudService.buscarTodasLasSolicitudes();

        model.addAttribute("solicitudes", solicitudes);

        return "admin/solicitudes";
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
}

