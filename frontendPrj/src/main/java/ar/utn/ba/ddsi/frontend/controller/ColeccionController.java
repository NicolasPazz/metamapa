package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.frontend.service.ColeccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/colecciones")
public class ColeccionController {
    private final ColeccionService coleccionService;

    @GetMapping
    public String listarColecciones(Model model) {
        List<ColeccionDTO> colecciones = coleccionService.getAllColecciones();
        model.addAttribute("colecciones", colecciones);
        model.addAttribute("titulo", "Listado de colecciones");

        return "colecciones/listacolecciones";
    }

    @GetMapping("/{handle}/hechos")
    public String listarHechosDeColeccion(Model model, @PathVariable String handle) {
        List<HechoDTO> hechos = coleccionService.getHechosColeccionByHandle(handle);
        model.addAttribute("hechos", hechos);
        model.addAttribute("titulo", "Hechos");
        model.addAttribute("solicitud", new SolicitudDTO());

        return "hechos/hechosencoleccion";
    }



}
