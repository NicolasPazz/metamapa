package ar.utn.ba.ddsi.frontend.controller;
import ar.utn.ba.ddsi.frontend.models.PageResponse;
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
import java.util.Map;

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
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String fuente,
            @RequestParam(defaultValue = "0") int page)
    {
        // Obtener hechos paginados del servicio (ya procesados)
        PageResponse<HechoDTO> pageResponse = hechoService.getAllHechosPaginados(
            null, ubicacion, fecha_reporte_hasta, fecha_reporte_desde, categoria, fuente, page, 100);
        List<String> listaCategorias = hechoService.obtenerCategoriasUnicas();
        List<String> listaProvincias = hechoService.obtenerProvinciasConHechos();
        List<String> listaFuentes = hechoService.obtenerFuentesConHechos();

        model.addAttribute("hechos", pageResponse.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("hasNextPage", pageResponse.isHasNext());
        model.addAttribute("titulo", "Hechos");
        model.addAttribute("categorias", listaCategorias);
        model.addAttribute("provincias", listaProvincias);
        model.addAttribute("fuentes", listaFuentes);
        // En /hechos: no mostrar botón Hechos, solo mostrar Colecciones
        model.addAttribute("showHechosBtn", false);
        model.addAttribute("showColeccionesBtn", true);
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
    public String hechosMapa(Model model, 
                              @RequestParam(required = false) Long hechoId,
                              @RequestParam(required = false) Long fuenteId,
                              @RequestParam(required = false) Long idOrigen) throws JsonProcessingException {
        Map<String, Object> datosMapa = hechoService.getDatosMapa(hechoId, fuenteId, idOrigen);
        datosMapa.forEach(model::addAttribute);
        model.addAttribute("titulo", "Mapa");
        return "mapa/mapa";
    }
}
