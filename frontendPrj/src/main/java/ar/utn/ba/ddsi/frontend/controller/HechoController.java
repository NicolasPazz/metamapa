package ar.utn.ba.ddsi.frontend.controller;
import ar.utn.ba.ddsi.frontend.models.PageResponse;
import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.HechoFormDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.HechoSolicitudInputDTO;
import ar.utn.ba.ddsi.frontend.service.HechoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/hechos")
@Slf4j
public class HechoController {

    private final HechoService hechoService;
    
    @Value("${app.pagination.page-size:18}")
    private int pageSize;
    @GetMapping
    public String listarHechos(Model model,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_reporte_hasta,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_reporte_desde,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String fuente,
            @RequestParam(required = false) String modo_navegacion,
            @RequestParam(defaultValue = "0") int page)
    {
        // Obtener hechos paginados del servicio (ya procesados)
        PageResponse<HechoDTO> pageResponse = hechoService.getAllHechosPaginados(
            null, ubicacion, fecha_reporte_hasta, fecha_reporte_desde, categoria, fuente, page, pageSize);
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
        List<String> listaCategorias = hechoService.obtenerCategoriasUnicas();
        model.addAttribute("categorias", listaCategorias);
        return "hechos/nuevo";
    }

    @PostMapping("/crear")
    public String crearHecho(@ModelAttribute HechoFormDTO formDTO, Model model) {
        try {
            hechoService.crearHecho(formDTO);
            return "hechos/exito";

        } catch (WebClientResponseException e) {

            String mensajeErrorDelBackend = e.getResponseBodyAsString();


            model.addAttribute("error", "El servidor rechazó la solicitud: " + mensajeErrorDelBackend);

            model.addAttribute("categorias", hechoService.obtenerCategoriasUnicas());

            return "hechos/nuevo";
        } catch (Exception e) {

            e.printStackTrace();
            model.addAttribute("error", "Error inesperado: " + e.getMessage());
            model.addAttribute("categorias", hechoService.obtenerCategoriasUnicas());
            return "hechos/nuevo";
        }
    }


    @GetMapping("/mapa")
    public String hechosMapa(Model model, 
                              @RequestParam(required = false) Long hechoId,
                              @RequestParam(required = false) Long fuenteId,
                              @RequestParam(required = false) Long idOrigen) {
        Map<String, Object> datosMapa = hechoService.getDatosMapa(hechoId, fuenteId, idOrigen);
        datosMapa.forEach(model::addAttribute);
        model.addAttribute("titulo", "Mapa");
        return "mapa/mapa";
    }
}
