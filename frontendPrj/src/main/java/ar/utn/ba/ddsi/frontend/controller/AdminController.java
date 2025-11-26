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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
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
}