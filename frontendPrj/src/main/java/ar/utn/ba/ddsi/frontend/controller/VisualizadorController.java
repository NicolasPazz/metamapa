// java
package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.service.VisualizadorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class VisualizadorController {
/*
    private final VisualizadorService service;

    public VisualizadorController(VisualizadorService service) {
        this.service = service;
    }

    // 1\.` Listado de Colecciones`
    @GetMapping("/colecciones")
    public String mostrarListadoColecciones(Model model) {
        // model.addAttribute("colecciones", service.obtenerColecciones());
        return "listado-colecciones";
    }

    // 2\.` Visualización y Filtrado de Hechos`
    @GetMapping("/hechos/filtrado")
    public String mostrarFiltradoHechos(Model model) {
        // model.addAttribute("hechos", service.obtenerHechosFiltrados(...));
        return "filtrado-hechos";
    }

    // 3\.` Detalles del Hecho`
    @GetMapping("/hechos/{id}")
    public String mostrarDetalleHecho(@PathVariable Long id, Model model) {
        HechoOutputDTO hechoDTO = service.obtenerHechoPorId(id);
        model.addAttribute("hechoDTO", hechoDTO);
        return "detalle-hecho";
    }

    // 4\.` Formulario de Solicitud de Eliminación`
    @GetMapping("/solicitudes/eliminar/{idHecho}")
    public String mostrarFormularioEliminacion(@PathVariable Long idHecho, Model model) {
        SolicitudInputDTO dto = new SolicitudInputDTO();
        dto.setIdHecho(idHecho);
        model.addAttribute("solicitudEliminacionDTO", dto);
        return "solicitud-eliminacion";
    }

    @PostMapping("/solicitudes/eliminar")
    public String procesarSolicitudEliminacion(
            @Valid @ModelAttribute("solicitudEliminacionDTO") SolicitudInputDTO dto,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "solicitud-eliminacion";
        }
        // service.procesarSolicitudEliminacion(dto);
        return "redirect:/dashboard";
    }*/
}