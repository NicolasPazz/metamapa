// java
package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.frontend.service.AdminService;
import ar.utn.ba.ddsi.frontend.service.ColeccionService;
import ar.utn.ba.ddsi.frontend.service.HechoService;
import ar.utn.ba.ddsi.frontend.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j

public class AdminController {
    private final ColeccionService coleccionService;
    private final HechoService hechoService;
    private final SolicitudService solicitudService;
    private final AdminService adminService;

    @GetMapping("/admin")
    public String mostrarMenu(Model model) {
        List<ColeccionDTO> colecciones = coleccionService.getAllColecciones();
        List<HechoDTO> hechos = hechoService.getAllHechos();
        List<SolicitudDTO> solicitudes = adminService.getAllSolicitudes();
        //model.addAttribute("coleccionesLimit", colecciones.subList(0,2));
        model.addAttribute("hechosLimit", hechos.subList(3,5));
        //model.addAttribute("solicitudesLimit", solicitudes.subList(0,2));
        model.addAttribute("colecciones", colecciones);
        model.addAttribute("hechos", hechos);
        model.addAttribute("solicitudes", solicitudes);
        return "admin/admin";
    }

    @PostMapping("/admin/actualizarSolicitud")
    public String actualizarSolicitud(@RequestParam("id") Long solicitudId,
                                      @RequestParam("estado") String estado,
                                      RedirectAttributes redirectAttributes) {
        try {
            // Pass the ID and new status to your service
            adminService.actualizarSolicitud(solicitudId, estado);
            redirectAttributes.addFlashAttribute("success", "Solicitud actualizada!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar.");
        }
        return "redirect:/admin/admin";
    }


    @PostMapping("/admin/subir-csv")
    public String procesarCsv(@RequestParam("archivo") MultipartFile archivo, Model model) {
        log.info("Recibiendo archivo: {}, tamaño: {} bytes", archivo.getOriginalFilename(), archivo.getSize());

        try {
            // Llamamos al servicio que se comunica con el otro sistema
            adminService.enviarArchivoAlBackend(archivo);
            model.addAttribute("mensaje", "Archivo enviado exitosamente para procesamiento.");
        } catch (Exception e) {
            log.error("Error al subir archivo", e);
            model.addAttribute("mensaje", "Error: " + e.getMessage());
        }

        return "/admin/admin"; // Volvemos a la vista admin.html
    }
}

