package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.PageResponse;
import ar.utn.ba.ddsi.frontend.models.dtos.ColeccionDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.frontend.service.ColeccionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/colecciones")
public class ColeccionController {
    private final ColeccionService coleccionService;
    
    @Value("${app.pagination.page-size:18}")
    private int pageSize;

    @GetMapping
    public String listarColecciones(Model model,
            @RequestParam(defaultValue = "0") int page) {
        log.info("[ColeccionController] - GET - /colecciones - page: {}", page);
        PageResponse<ColeccionDTO> pageResponse =
            coleccionService.getAllColeccionesPaginadas(page, pageSize);

        model.addAttribute("colecciones", pageResponse.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("hasNextPage", pageResponse.isHasNext());
        model.addAttribute("titulo", "Listado de colecciones");
        model.addAttribute("showHechosBtn", true);
        model.addAttribute("showColeccionesBtn", false);
        model.addAttribute("showReportarHechoBtn", false);

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
            @RequestParam(required = false) String fuente,
            @RequestParam(required = false) String modo,
            @RequestParam(defaultValue = "0") int page) {
        log.info("[ColeccionController] - GET - /colecciones/{}/hechos - handle: {}, ubicacion: {}, fecha_reporte_hasta: {}, fecha_reporte_desde: {}, categoria: {}, fuente: {}, modo: {}, page: {}",
                handle, handle, ubicacion, fecha_reporte_hasta, fecha_reporte_desde, categoria, fuente, modo, page);
        PageResponse<HechoDTO> pageResponse =
            coleccionService.getHechosColeccionPaginados(
                handle, ubicacion, fecha_reporte_hasta, fecha_reporte_desde, categoria, fuente, modo, page, pageSize);
        List<String> listaCategorias = coleccionService.getCategoriasUnicas();
        List<String> listaProvincias = coleccionService.getProvinciasConHechos();
        List<String> listaFuentes = coleccionService.getFuentesConHechos();

        model.addAttribute("hechos", pageResponse.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("hasNextPage", pageResponse.isHasNext());
        model.addAttribute("titulo", "Hechos de colección");
        model.addAttribute("categorias", listaCategorias);
        model.addAttribute("provincias", listaProvincias);
        model.addAttribute("fuentes", listaFuentes);
        model.addAttribute("solicitud", new SolicitudDTO());
        model.addAttribute("esColeccion", true);
        model.addAttribute("handleColeccion", handle);
        model.addAttribute("showHechosBtn", true);
        model.addAttribute("showColeccionesBtn", true);
        model.addAttribute("showReportarHechoBtn", false);

        return "hechos/hechosencoleccion";
    }

    @GetMapping("/{handle}/mapa")
    public String hechosColeccionMapa(
            Model model,
            @PathVariable String handle) {
        log.info("[ColeccionController] - GET - /colecciones/{}/mapa - handle: {}", handle, handle);
        List<HechoDTO> hechos = coleccionService.getHechosColeccionByHandle(
                handle, null, null, null, null, null);
        model.addAttribute("hechos", hechos);
        model.addAttribute("titulo", "Mapa de Colección");
        model.addAttribute("handleColeccion", handle);
        model.addAttribute("showReportarHechoBtn", false);
        return "mapa/mapa";
    }

}
