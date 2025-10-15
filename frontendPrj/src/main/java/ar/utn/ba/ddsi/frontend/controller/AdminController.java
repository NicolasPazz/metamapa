// java
package ar.utn.ba.ddsi.frontend.controller;

import com.metamapa.dtos.CrearColeccionInputDTO;
import com.metamapa.dtos.SolicitudModificarAdminInputDTO;
import com.metamapa.dtos.ActualizarFuentesColeccionDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AdminController {

    @GetMapping("/dashboard")
    public String mostrarDashboard(Model model) {
        return "dashboard";
    }

    @GetMapping("/colecciones/nueva")
    public String mostrarFormularioColeccion(Model model) {
        model.addAttribute("coleccionDTO", new CrearColeccionInputDTO());
        return "crear-coleccion";
    }

    @PostMapping("/colecciones")
    public String procesarColeccion(@Valid @ModelAttribute("coleccionDTO") CrearColeccionInputDTO dto,
                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "crear-coleccion";
        }
        return "redirect:/colecciones";
    }

    @GetMapping("/colecciones/{id}/fuentes")
    public String mostrarConfiguracionFuentes(@PathVariable Long id, Model model) {
        return "configurar-fuentes";
    }

    @PostMapping("/colecciones/{id}/fuentes")
    public String procesarFuentes(@PathVariable Long id,
                                  @Valid @ModelAttribute("fuentesDTO") ActualizarFuentesColeccionDTO dto,
                                  BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "configurar-fuentes";
        }
        return "redirect:/colecciones/" + id;
    }

    @GetMapping("/solicitudes/revisar")
    public String mostrarRevisionSolicitudes(Model model) {
        return "revision-solicitudes";
    }

    @PostMapping("/solicitudes/{id}/aprobar")
    public String procesarRevisionSolicitud(@PathVariable Long id,
                                            @ModelAttribute SolicitudModificarAdminInputDTO dto) {
        return "redirect:/solicitudes/revisar";
    }
}