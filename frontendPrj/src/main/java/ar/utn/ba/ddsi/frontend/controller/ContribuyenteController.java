// java
package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.service.ContribuyenteService;
import com.metamapa.dtos.SolicitudHechoInputContribuyenteDTO;
import com.metamapa.dtos.SolicitudModificarUserInputDTO;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ContribuyenteController {

    private final ContribuyenteService service;

    public ContribuyenteController(ContribuyenteService service) {
        this.service = service;
    }

    @GetMapping("/hechos/nuevo")
    public String mostrarFormularioCreacion(Model model) {
        model.addAttribute("solicitudHechoDTO", new SolicitudHechoInputContribuyenteDTO());
        return "crear-hecho";
    }

    @PostMapping("/hechos")
    public String procesarCreacionHecho(@Valid @ModelAttribute("solicitudHechoDTO") SolicitudHechoInputContribuyenteDTO dto,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "crear-hecho";
        }
        service.guardarHecho(dto);
        return "redirect:/dashboard";
    }

    @GetMapping("/hechos/{id}/editar")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        SolicitudModificarUserInputDTO dto = service.obtenerHechoParaEdicion(id);
        model.addAttribute("solicitudModificarDTO", dto);
        return "editar-hecho";
    }

    @PostMapping("/hechos/{id}/editar")
    public String procesarEdicionHecho(@PathVariable Long id,
                                       @Valid @ModelAttribute("solicitudModificarDTO") SolicitudModificarUserInputDTO dto,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "editar-hecho";
        }
        service.actualizarHecho(id, dto);
        return "redirect:/hechos/" + id;
    }
}