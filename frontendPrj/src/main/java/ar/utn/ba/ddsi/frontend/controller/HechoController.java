package ar.utn.ba.ddsi.frontend.controller;
import ar.utn.ba.ddsi.frontend.models.dtos.ContenidoMultimediaDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.*;
import ar.utn.ba.ddsi.frontend.service.HechoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/hechos")
@Slf4j
public class HechoController {

    private final HechoService hechoService;
    @GetMapping
    public String listarHechos(Model model,

            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_reporte_hasta,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_reporte_desde,
            @RequestParam(required = false) String categoria
            )
    {
        List<HechoDTO> hechos = hechoService.getAllHechosFilter(null, ubicacion, fecha_reporte_hasta, fecha_reporte_desde, categoria);
        List<String> listaCategorias = hechoService.obtenerCategoriasUnicas();
        List<String> listaProvincias = hechoService.obtenerProvinciasConHechos();

        model.addAttribute("hechos", hechos);
        model.addAttribute("hechos_encontrados", hechos.size());
        model.addAttribute("titulo", "Hechos");
        model.addAttribute("categorias", listaCategorias);
        model.addAttribute("provincias", listaProvincias);
        return "hechos/hechosencoleccion";

    }

    @GetMapping("/nuevo")
    public String nuevoHecho(Model model){
        return "hechos/nuevo";
    }

    @PostMapping("/crear")
    public String crearHecho(Model model, HechoFormDTO hechoInput)

            {
                try{
                    hechoService.crearHecho(hechoInput);
                    return "hechos/exito";
                }
                catch (Exception e) {
                    log.error("Error al crear Hecho", e);
                    model.addAttribute("mensaje", "Error: " + e.getMessage());
                    return "hechos/nuevo";
                }
    }


    @GetMapping("/mapa")
    public String hechosMapa(Model model) throws JsonProcessingException {
        List<HechoDTO> hechos = hechoService.getAllHechos();
        model.addAttribute("hechos", hechos);
        model.addAttribute("titulo", "Mapa");
        return "mapa/mapa";
    }
}
