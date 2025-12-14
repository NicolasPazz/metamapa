package ar.utn.ba.ddsi.frontend.controller;
import ar.utn.ba.ddsi.frontend.models.dtos.HechoFormDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.frontend.service.HechoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/hechos")
@Slf4j
public class HechoController {
    private final HechoService hechoService;

     @GetMapping
    public String listarHechos(Model model) {
         try {
             List<HechoDTO> hechos = hechoService.getAllHechos();
             model.addAttribute("hechos", hechos);
             model.addAttribute("titulo", "Hechos");
             model.addAttribute("solicitud", new SolicitudDTO());

             return "hechos/hechosencoleccion";

         }
         catch (Exception e) {
             log.error(e.getMessage());
         }
         return "/hechos/nuevo";
            }

    @GetMapping("/nuevo")
    public String nuevoHecho(Model model){

        return "hechos/nuevo";
    }
    @PostMapping("/crear")
    public String crearHecho(Model model, HechoFormDTO HechoInput)
    {
        HechoDTO hechoDTO = new HechoDTO();
        try{
            hechoDTO.FormtoDTO(HechoInput);
            hechoService.guardaHecho(hechoDTO);
        }
        catch (Exception e) {
            log.error("Error al crear Hecho", e);
            model.addAttribute("mensaje", "Error: " + e.getMessage());
        }
        return "hechos/nuevo";
    }




/*
    @GetMapping
    public String listarHechos(Model model,

            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_reporte_hasta,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_reporte_desde, // HTML date input envía yyyy-MM-dd por defecto// HTML date input envía yyyy-MM-dd por defecto
            @RequestParam(required = false) String modo,
            @RequestParam(required = false) String categoria
            )
    {

        List<HechoDTO> hechos = hechoService.getAllHechosFilter(modo, ubicacion, fecha_reporte_hasta, fecha_reporte_desde, categoria);

        List<String> listaCategorias = hechoService.obtenerCategoriasUnicas();

        model.addAttribute("hechos", hechos);
        model.addAttribute("hechos_encontrados", hechos.size());
        model.addAttribute("titulo", "Hechos");
        model.addAttribute("categorias", listaCategorias);
        //model.addAttribute("solicitud", new SolicitudDTO());


        return "hechos/hechosencoleccion";

    }

    */
    // TODO: No se deberia hacer el filtrado en el front, deberia venir filtrado del agregador
    // TODO: Por el momento funciona pero esta mal.




    @GetMapping("/mapa")
    public String hechosMapa(Model model) throws JsonProcessingException {
        List<HechoDTO> hechos = hechoService.getAllHechos();
        model.addAttribute("hechos", hechos);
        model.addAttribute("titulo", "Mapa");
        return "mapa/mapa";
    }
}
