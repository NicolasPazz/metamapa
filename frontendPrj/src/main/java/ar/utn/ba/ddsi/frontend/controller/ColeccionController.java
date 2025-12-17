package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.frontend.service.ColeccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
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
    public String listarHechosDeColeccion(
            Model model,
            @PathVariable String handle,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_reporte_hasta,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_reporte_desde,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String modo) {

        List<HechoDTO> hechos = coleccionService.getHechosColeccionByHandle(
                handle, ubicacion, fecha_reporte_hasta, fecha_reporte_desde, categoria, modo);
        List<String> listaCategorias = coleccionService.getCategoriasUnicas();
        List<String> listaProvincias = coleccionService.getProvinciasConHechos();

        model.addAttribute("hechos", hechos);
        model.addAttribute("hechos_encontrados", hechos.size());
        model.addAttribute("titulo", "Hechos de colección");
        model.addAttribute("categorias", listaCategorias);
        model.addAttribute("provincias", listaProvincias);
        model.addAttribute("solicitud", new SolicitudDTO());
        model.addAttribute("esColeccion", true);
        model.addAttribute("handleColeccion", handle);

        return "hechos/hechosencoleccion";
    }



}
