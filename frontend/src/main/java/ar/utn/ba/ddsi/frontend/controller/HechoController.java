package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.PageResponse;
import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.HechoFormDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudInputDTO;
import ar.utn.ba.ddsi.frontend.service.HechoService;
import ar.utn.ba.ddsi.frontend.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/hechos")
@Slf4j
public class HechoController {

    private final HechoService hechoService;
    private final SolicitudService solicitudService;
    
    @Value("${app.pagination.page-size:18}")
    private int pageSize;
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Double.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text == null || text.trim().isEmpty()) {
                    setValue(null);
                } else {
                    try {
                        setValue(Double.parseDouble(text.trim()));
                    } catch (NumberFormatException e) {
                        throw new IllegalArgumentException("Valor numérico inválido: " + text);
                    }
                }
            }
            
            @Override
            public String getAsText() {
                Double value = (Double) getValue();
                return value != null ? value.toString() : "";
            }
        });
    }
    
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
        log.info("[HechoController] - GET - /hechos - ubicacion: {}, fecha_reporte_hasta: {}, fecha_reporte_desde: {}, categoria: {}, fuente: {}, modo_navegacion: {}, page: {}",
                ubicacion, fecha_reporte_hasta, fecha_reporte_desde, categoria, fuente, modo_navegacion, page);
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
        model.addAttribute("showHechosBtn", false);
        model.addAttribute("showColeccionesBtn", true);
        model.addAttribute("showReportarHechoBtn", true);
        model.addAttribute("showMisHechosBtn", true);
        return "hechos/hechosencoleccion";

    }

    @GetMapping("/mis-hechos")
    public String misHechos(Model model) {
        log.info("[HechoController] - GET - /hechos/mis-hechos");
        Long contribuyenteId = solicitudService.obtenerContribuyenteIdDeSesion();
        if (contribuyenteId == null) {
            return "redirect:/login";
        }
        
        List<SolicitudInputDTO> solicitudes = solicitudService.obtenerSolicitudesPorContribuyente(contribuyenteId);
        
        for (SolicitudInputDTO solicitud : solicitudes) {
            if (solicitud.getFechaDeCargaSolicitud() != null) {
                LocalDateTime ahora = LocalDateTime.now();
                long diasPasados = ChronoUnit.DAYS.between(solicitud.getFechaDeCargaSolicitud(), ahora);
                int diasRestantes = (int) Math.max(0, 7 - diasPasados);
                solicitud.setDiasRestantesEdicion(diasRestantes);
            } else {
                solicitud.setDiasRestantesEdicion(null);
            }
            
            if (solicitud.getIdHecho() != null && Boolean.TRUE.equals(solicitud.getHechoEnviado())) {
                HechoDTO hechoAgregador = hechoService.getHechoByFuenteIdAndIdOrigen(1L, solicitud.getIdHecho());
                if (hechoAgregador != null && hechoAgregador.getId() != null) {
                    solicitud.setIdHechoAgregador(hechoAgregador.getId());
                }
            }
        }
        
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("contribuyenteId", contribuyenteId);
        model.addAttribute("titulo", "Mis Hechos");
        model.addAttribute("showReportarHechoBtn", true);
        model.addAttribute("showMisHechosBtn", false);
        return "hechos/misHechos";
    }

    @GetMapping("/nuevo")
    public String nuevoHecho(Model model){
        log.info("[HechoController] - GET - /hechos/nuevo");
        List<String> listaCategorias = hechoService.obtenerCategoriasUnicas();
        model.addAttribute("categorias", listaCategorias);
        model.addAttribute("titulo", "Nuevo Hecho");
        model.addAttribute("showReportarHechoBtn", true);
        return "hechos/nuevo";
    }

    @PostMapping(value = "/crear", consumes = "multipart/form-data")
    @ResponseBody
    public ResponseEntity<?> crearHecho(@ModelAttribute HechoFormDTO formDTO) {
        log.info("[HechoController] - POST - /hechos/crear - titulo: {}, descripcion: {}, latitud: {}, longitud: {}, categoria: {}, fechaAcontecimiento: {}, contenidoMultimedia: {}",
                formDTO.getTitulo(), formDTO.getDescripcion(), formDTO.getLatitud(), formDTO.getLongitud(),
                formDTO.getCategoria(), formDTO.getFechaAcontecimiento(),
                formDTO.getContenidoMultimedia() != null ? formDTO.getContenidoMultimedia().size() : 0);
        try {
            hechoService.crearHecho(formDTO);
            return ResponseEntity.ok().build();
        } catch (MaxUploadSizeExceededException e) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body("El tamaño del archivo excede el límite permitido de 50 MB. " +
                          "Por favor, comprima o reduzca el tamaño de los archivos multimedia e intente nuevamente.");
        } catch (WebClientResponseException e) {
            String mensajeErrorDelBackend = e.getResponseBodyAsString();
            String mensaje = !mensajeErrorDelBackend.isEmpty()
                            ? mensajeErrorDelBackend 
                            : "Error al crear el hecho. Por favor, intente nuevamente.";
            return ResponseEntity.status(e.getStatusCode()).body(mensaje);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error inesperado: " + e.getMessage());
        }
    }

    @GetMapping("/mapa")
    public String hechosMapa(Model model, 
                              @RequestParam(required = false) Long hechoId,
                              @RequestParam(required = false) Long fuenteId,
                              @RequestParam(required = false) Long idOrigen) {
        log.info("[HechoController] - GET - /hechos/mapa - hechoId: {}, fuenteId: {}, idOrigen: {}", hechoId, fuenteId, idOrigen);
        Map<String, Object> datosMapa = hechoService.getDatosMapa(hechoId, fuenteId, idOrigen);
        datosMapa.forEach(model::addAttribute);
        model.addAttribute("titulo", "Mapa");
        model.addAttribute("showReportarHechoBtn", false);
        return "mapa/mapa";
    }
}
