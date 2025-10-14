package ar.utn.ba.ddsi.frontend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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

        return "hechos/hechosencoleccion";
    }

}
