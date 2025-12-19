// java
package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.dtos.*;
import ar.utn.ba.ddsi.frontend.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Logger;
import org.springframework.cglib.core.Local;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j

public class AdminController {
    private final ColeccionService coleccionService;
    private final HechoService hechoService;
    private final SolicitudService solicitudService;
    private final AdminService adminService;
    private final ObjectMapper objectMapper;
    private final FuenteService fuenteService;

    //@PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/admin")
    public String mostrarMenu(Model model) {
        log.info("GET /admin");
        List<ColeccionDTO> colecciones = coleccionService.getAllColecciones();
        List<HechoDTO> hechos = hechoService.getAllHechos();
        List<SolicitudDTO> solicitudes = adminService.getAllSolicitudes();
        List<FuenteDTO> fuentes = fuenteService.getAllFuentes();
        Map<Long, String> mapFuentes = fuentes.stream()
                .collect(Collectors.toMap(FuenteDTO::getId, FuenteDTO::getNombre));

        model.addAttribute("titulo", "Panel De Control");
        //model.addAttribute("coleccionesLimit", colecciones.subList(0,2));
        // Mostrar hechos limitados de forma segura
        if (hechos != null && hechos.size() > 3) {
            int endIndex = Math.min(5, hechos.size());
            model.addAttribute("hechosLimit", hechos.subList(3, endIndex));
        } else {
            model.addAttribute("hechosLimit", java.util.Collections.emptyList());
        }
        //model.addAttribute("solicitudesLimit", solicitudes.subList(0,2));
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
        log.info("POST /admin/actualizarSolicitud - id: {}, estado: {}", solicitudId, estado);
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
        log.info("POST /admin/subir-csv - archivo: {} ({} bytes, tipo: {})",
                archivo.getOriginalFilename(), archivo.getSize(), archivo.getContentType());

        try {
            // Llamamos al servicio que se comunica con el otro sistema
            adminService.enviarArchivoAlBackend(archivo);
            model.addAttribute("mensaje", "Archivo enviado exitosamente para procesamiento.");
        } catch (Exception e) {
            log.error("Error al subir archivo", e);
            model.addAttribute("mensaje", "Error: " + e.getMessage());
        }

        return "redirect:/admin"; // Volvemos a la vista admin.html
    }

    @PostMapping("/admin/crear-coleccion")
    public String crearColeccion(@Valid @ModelAttribute CrearColeccionDTO coleccionDTO, Model model) {
        log.info("POST /admin/crear-coleccion - titulo: {}, descripcion: {}, criterios: {}, algoritmoConsenso: {}, fuenteId: {}",
                coleccionDTO.getTitulo(), coleccionDTO.getDescripcion(), coleccionDTO.getCriterios(),
                coleccionDTO.getAlgoritmoConsenso(), coleccionDTO.getFuenteId());

        if(!coleccionDTO.isValid()) {
            model.addAttribute("mensaje", "Error: faltan datos para la colección");
            return "redirect:/admin";
        }

        try {
            // Llamamos al servicio que se comunica con el otro sistema
            adminService.crearColeccion(coleccionDTO);
            model.addAttribute("mensaje", "Colección creada correctamente.");
        } catch (Exception e) {
            log.error("Error al crear coleccion", e);
            model.addAttribute("mensaje", "Error: " + e.getMessage());
        }

        return "redirect:/admin"; // Volvemos a la vista admin.html
    }

    @PostMapping("/admin/actualizar-coleccion")
    public String actualizarColeccion(@Valid @ModelAttribute CrearColeccionDTO coleccionDTO, @RequestParam("handle") String handle, Model model) {
        log.info("POST /admin/actualizar-coleccion - handle: {}, titulo: {}, descripcion: {}, criterios: {}, algoritmoConsenso: {}, fuenteId: {}",
                handle, coleccionDTO.getTitulo(), coleccionDTO.getDescripcion(), coleccionDTO.getCriterios(),
                coleccionDTO.getAlgoritmoConsenso(), coleccionDTO.getFuenteId());
        if(!coleccionDTO.isValid()) {
            model.addAttribute("mensaje", "Error: faltan datos para la colección");
            return "redirect:/admin";
        }

        try {
            // Llamamos al servicio que se comunica con el otro sistema
            adminService.actualizarColeccion(coleccionDTO, handle);
            model.addAttribute("mensaje", "Colección actualizada correctamente.");
        } catch (Exception e) {
            log.error("Error al actualizar coleccion", e);
            model.addAttribute("mensaje", "Error: " + e.getMessage());
        }

        return "redirect:/admin"; // Volvemos a la vista admin.html
    }
    @GetMapping("admin/solicitudes")
    public String verPanelSolicitudes(Model model) {
        List<SolicitudInputDTO> solicitudes = solicitudService.buscarTodasLasSolicitudes();
        model.addAttribute("solicitudes", solicitudes);
        return "admin/solicitudes";
    }

}

