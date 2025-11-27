package ar.utn.ba.ddsi.frontend.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.frontend.service.HechoService;
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
@RequestMapping("/hechos")
public class HechoController {
    private final HechoService hechoService;

    /* @GetMapping
    public String listarHechos(Model model) {
        List<HechoDTO> hechos = hechoService.getAllHechos();
        model.addAttribute("hechos", hechos);
        model.addAttribute("titulo", "Hechos");
        model.addAttribute("solicitud", new SolicitudDTO());

        return "hechos/hechosencoleccion";
    } */



    @GetMapping
    public String listarHechos(
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_reporte_hasta,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_reporte_desde, // HTML date input envía yyyy-MM-dd por defecto// HTML date input envía yyyy-MM-dd por defecto
            @RequestParam(required = false) String modo,
            @RequestParam(required = false) String categoria,
            Model model)
    {

        List<HechoDTO> hechos = hechoService.getAllHechosFilter(modo, ubicacion, fecha_reporte_hasta, fecha_reporte_desde, categoria);

        model.addAttribute("hechos", hechos);
        model.addAttribute("titulo", "Hechos");
        model.addAttribute("solicitud", new SolicitudDTO());
        return "hechos/hechosencoleccion";

    } // TODO: hay algo que no esta funcionando, tendria que llamar al hecho service y traer con los filtros aplicados



    @GetMapping("/mapa")
    public String hechosMapa(Model model) throws JsonProcessingException {
        List<HechoDTO> hechos = hechoService.getAllHechos();
        model.addAttribute("hechos", hechos);
        model.addAttribute("titulo", "Mapa");
        return "mapa/mapa";
    }
}
