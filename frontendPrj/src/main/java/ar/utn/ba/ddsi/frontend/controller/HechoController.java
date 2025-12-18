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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        // Mostrar botón "Reportar nuevo hecho" solo en /hechos
        model.addAttribute("showReportarHechoBtn", true);
        return "hechos/hechosencoleccion";

    }

    @GetMapping("/nuevo")
    public String nuevoHecho(Model model){
        List<String> listaCategorias = hechoService.obtenerCategoriasUnicas();
        model.addAttribute("categorias", listaCategorias);
        model.addAttribute("titulo", "Nuevo Hecho");
        model.addAttribute("showReportarHechoBtn", false);
        return "hechos/nuevo";
    }

    @PostMapping(value = "/crear", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<?> crearHecho(@ModelAttribute HechoFormDTO formDTO) {
        try {
            // Validar fecha si está presente
            if (formDTO.getFechaAcontecimiento() != null) {
                if (formDTO.getFechaAcontecimiento().isAfter(LocalDateTime.now())) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("La fecha y hora no pueden ser futuras.");
                }
            }

            hechoService.crearHecho(formDTO);
            return ResponseEntity.ok().build();

        } catch (WebClientResponseException e) {
            String mensajeErrorDelBackend = e.getResponseBodyAsString();
            log.error("Error del backend al crear hecho: {}", mensajeErrorDelBackend);
            return ResponseEntity.status(e.getStatusCode())
                    .body(mensajeErrorDelBackend != null && !mensajeErrorDelBackend.isEmpty() 
                            ? mensajeErrorDelBackend 
                            : "Error al crear el hecho. Por favor, intente nuevamente.");
        } catch (Exception e) {
            log.error("Error inesperado al crear hecho: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
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
        model.addAttribute("showReportarHechoBtn", false);
        return "mapa/mapa";
    }
}
