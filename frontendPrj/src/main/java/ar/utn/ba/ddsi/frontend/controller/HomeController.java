package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.frontend.service.ColeccionService;
import ar.utn.ba.ddsi.frontend.service.HechoService;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final HechoService hechoService;
    private final ColeccionService coleccionService;

    public HomeController(HechoService hechoService, ColeccionService coleccionService) {
        this.hechoService = hechoService;
        this.coleccionService = coleccionService;
    }

    @GetMapping("/")
    public String landing(Model model) {
        // Obtener un solo hecho para la landing (optimizado)
        HechoDTO hecho = hechoService.getOneHecho();
        List<HechoDTO> hechos = hecho != null ? Collections.singletonList(hecho) : Collections.emptyList();

        // Obtener una sola colección para la landing (optimizado)
        ColeccionDTO coleccion = coleccionService.getOneColeccion();
        List<ColeccionDTO> colecciones = coleccion != null ? Collections.singletonList(coleccion) : Collections.emptyList();

        model.addAttribute("hechos", hechos);
        model.addAttribute("colecciones", colecciones);

        return "landing/landing";
    }

    @GetMapping("/terminos")
    public String terminos() {
        return "legal/terminos";
    }

    @GetMapping("/privacidad")
    public String privacidad() {
        return "legal/privacidad";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "legal/contacto";
    }

}
