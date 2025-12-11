package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.EstadoSolicitud;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.SolicitudEliminacion;
import ar.utn.ba.ddsi.agregador.dto.CategoriaEstadisticaDTO;
import ar.utn.ba.ddsi.agregador.dto.EstadisticasDTO;
import ar.utn.ba.ddsi.agregador.dto.HoraEstadisticaDTO;
import ar.utn.ba.ddsi.agregador.dto.ProvinciaEstadisticaDTO;
import ar.utn.ba.ddsi.agregador.repositorie.IColeccionesRepository;
import ar.utn.ba.ddsi.agregador.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.agregador.repositorie.ISolicitudesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EstadisticasService {

    private final IHechosRepository hechosRepository;
    private final IColeccionesRepository coleccionesRepository;
    private final ISolicitudesRepository solicitudesRepository;

    private EstadisticasDTO estadisticasCache;
    private LocalDateTime ultimaActualizacion;

    @Scheduled(fixedRate = 3600000)
    public void recalcularEstadisticas() {
        log.info("[EstadisticasService] Iniciando recálculo de estadísticas");
        estadisticasCache = calcularEstadisticas();
        ultimaActualizacion = LocalDateTime.now();
        log.info("[EstadisticasService] Estadísticas actualizadas a las {}", ultimaActualizacion);
    }

    public EstadisticasDTO obtenerEstadisticas() {
        if (estadisticasCache == null) {
            log.info("[EstadisticasService] No hay estadísticas en caché, calculando...");
            recalcularEstadisticas();
        }
        return estadisticasCache;
    }

    public CategoriaEstadisticaDTO obtenerCategoriaConMasHechos() {
        log.info("[EstadisticasService] Calculando categoría con más hechos");

        List<Hecho> hechos = hechosRepository.findAll().stream()
            .filter(Hecho::esVisible)
            .toList();

        Map<String, Long> conteoPorCategoria = hechos.stream()
            .filter(h -> h.getCategoria() != null && h.getCategoria().getNombre() != null)
            .collect(Collectors.groupingBy(
                h -> h.getCategoria().getNombre(),
                Collectors.counting()
            ));

        return conteoPorCategoria.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> CategoriaEstadisticaDTO.builder()
                .categoria(entry.getKey())
                .cantidad(entry.getValue())
                .build())
            .orElse(null);
    }

    public Map<String, ProvinciaEstadisticaDTO> obtenerProvinciaConMasHechosPorColeccion() {
        log.info("[EstadisticasService] Calculando provincia con más hechos por colección");

        List<Coleccion> colecciones = coleccionesRepository.findAll();
        Map<String, ProvinciaEstadisticaDTO> resultado = new HashMap<>();

        for (Coleccion coleccion : colecciones) {
            if (coleccion.getHechos() == null || coleccion.getHechos().isEmpty()) {
                continue;
            }

            Map<String, Long> conteoPorProvincia = coleccion.getHechos().stream()
                .filter(Hecho::esVisible)
                .filter(h -> h.getLatitud() != null && h.getLongitud() != null)
                .collect(Collectors.groupingBy(
                    this::obtenerProvincia,
                    Collectors.counting()
                ));

            conteoPorProvincia.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(entry -> resultado.put(
                    coleccion.getTitulo(),
                    ProvinciaEstadisticaDTO.builder()
                        .provincia(entry.getKey())
                        .cantidad(entry.getValue())
                        .build()
                ));
        }

        return resultado;
    }

    public ProvinciaEstadisticaDTO obtenerProvinciaConMasHechosPorCategoria(String categoria) {
        log.info("[EstadisticasService] Calculando provincia con más hechos para categoría: {}", categoria);

        List<Hecho> hechos = hechosRepository.findAll().stream()
            .filter(Hecho::esVisible)
            .filter(h -> h.getCategoria() != null &&
                         h.getCategoria().getNombre() != null &&
                         h.getCategoria().getNombre().equalsIgnoreCase(categoria))
            .toList();

        Map<String, Long> conteoPorProvincia = hechos.stream()
            .filter(h -> h.getLatitud() != null && h.getLongitud() != null)
            .collect(Collectors.groupingBy(
                this::obtenerProvincia,
                Collectors.counting()
            ));

        return conteoPorProvincia.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> ProvinciaEstadisticaDTO.builder()
                .provincia(entry.getKey())
                .cantidad(entry.getValue())
                .build())
            .orElse(null);
    }

    public Map<String, ProvinciaEstadisticaDTO> obtenerProvinciaPorTodasLasCategorias() {
        log.info("[EstadisticasService] Calculando provincia con más hechos para todas las categorías");

        List<Hecho> hechos = hechosRepository.findAll().stream()
            .filter(Hecho::esVisible)
            .filter(h -> h.getCategoria() != null && h.getCategoria().getNombre() != null)
            .toList();

        Set<String> categorias = hechos.stream()
            .map(h -> h.getCategoria().getNombre())
            .collect(Collectors.toSet());

        Map<String, ProvinciaEstadisticaDTO> resultado = new HashMap<>();

        for (String categoria : categorias) {
            ProvinciaEstadisticaDTO provinciaStats = obtenerProvinciaConMasHechosPorCategoria(categoria);
            if (provinciaStats != null) {
                resultado.put(categoria, provinciaStats);
            }
        }

        return resultado;
    }

    public HoraEstadisticaDTO obtenerHoraPicoPorCategoria(String categoria) {
        log.info("[EstadisticasService] Calculando hora pico para categoría: {}", categoria);

        List<Hecho> hechos = hechosRepository.findAll().stream()
            .filter(Hecho::esVisible)
            .filter(h -> h.getCategoria() != null &&
                         h.getCategoria().getNombre() != null &&
                         h.getCategoria().getNombre().equalsIgnoreCase(categoria))
            .filter(h -> h.getFechaDeCarga() != null)
            .toList();

        Map<Integer, Long> conteoPorHora = hechos.stream()
            .collect(Collectors.groupingBy(
                h -> h.getFechaDeCarga().getHour(),
                Collectors.counting()
            ));

        return conteoPorHora.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> HoraEstadisticaDTO.builder()
                .hora(entry.getKey())
                .cantidad(entry.getValue())
                .build())
            .orElse(null);
    }

    public Map<String, HoraEstadisticaDTO> obtenerHoraPicoParaTodasLasCategorias() {
        log.info("[EstadisticasService] Calculando hora pico para todas las categorías");

        List<Hecho> hechos = hechosRepository.findAll().stream()
            .filter(Hecho::esVisible)
            .filter(h -> h.getCategoria() != null && h.getCategoria().getNombre() != null)
            .filter(h -> h.getFechaDeCarga() != null)
            .toList();

        Set<String> categorias = hechos.stream()
            .map(h -> h.getCategoria().getNombre())
            .collect(Collectors.toSet());

        Map<String, HoraEstadisticaDTO> resultado = new HashMap<>();

        for (String categoria : categorias) {
            HoraEstadisticaDTO horaStats = obtenerHoraPicoPorCategoria(categoria);
            if (horaStats != null) {
                resultado.put(categoria, horaStats);
            }
        }

        return resultado;
    }

    public Long obtenerTotalSolicitudesSpam() {
        log.info("[EstadisticasService] Contando solicitudes de eliminación marcadas como spam");

        return solicitudesRepository.findAll().stream()
            .filter(s -> s.getEstadoActual() == EstadoSolicitud.RECHAZADA_SPAM)
            .count();
    }

    private EstadisticasDTO calcularEstadisticas() {
        log.info("[EstadisticasService] Calculando estadísticas completas");

        return EstadisticasDTO.builder()
            .categoriaConMasHechos(obtenerCategoriaConMasHechos())
            .provinciaConMasHechosPorColeccion(obtenerProvinciaConMasHechosPorColeccion())
            .provinciaConMasHechosPorCategoria(obtenerProvinciaPorTodasLasCategorias())
            .horasPicoPorCategoria(obtenerHoraPicoParaTodasLasCategorias())
            .totalSolicitudesSpam(obtenerTotalSolicitudesSpam())
            .fechaUltimaActualizacion(
                ultimaActualizacion != null ?
                    ultimaActualizacion.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) :
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            )
            .build();
    }

    private String obtenerProvincia(Hecho hecho) {
        // Lógica simplificada de mapeo de coordenadas a provincia
        // En producción, esto debería usar un servicio de geocodificación inversa
        // o una base de datos de provincias con sus límites geográficos

        Double lat = hecho.getLatitud();
        Double lon = hecho.getLongitud();

        // Rangos aproximados de provincias argentinas (ejemplo simplificado)
        if (lat >= -35 && lat <= -34 && lon >= -59 && lon <= -58) {
            return "Buenos Aires";
        } else if (lat >= -32 && lat <= -31 && lon >= -61 && lon <= -60) {
            return "Córdoba";
        } else if (lat >= -33 && lat <= -32 && lon >= -69 && lon <= -68) {
            return "Mendoza";
        } else if (lat >= -27 && lat <= -26 && lon >= -55 && lon <= -54) {
            return "Misiones";
        } else if (lat >= -55 && lat <= -54 && lon >= -69 && lon <= -67) {
            return "Tierra del Fuego";
        } else if (lat >= -25 && lat <= -24 && lon >= -66 && lon <= -65) {
            return "Salta";
        } else if (lat >= -29 && lat <= -28 && lon >= -67 && lon <= -66) {
            return "La Rioja";
        } else if (lat >= -32 && lat <= -31 && lon >= -65 && lon <= -64) {
            return "San Luis";
        }

        return "Provincia Desconocida";
    }

    public String exportarEstadisticasCSV() {
        log.info("[EstadisticasService] Exportando estadísticas a CSV");

        EstadisticasDTO stats = obtenerEstadisticas();
        StringBuilder csv = new StringBuilder();

        // Header
        csv.append("Tipo,Categoria/Coleccion,Valor,Cantidad\n");

        // Categoría con más hechos
        if (stats.getCategoriaConMasHechos() != null) {
            csv.append(String.format("Categoria con mas hechos,%s,,%d\n",
                stats.getCategoriaConMasHechos().getCategoria(),
                stats.getCategoriaConMasHechos().getCantidad()));
        }

        // Provincias por colección
        if (stats.getProvinciaConMasHechosPorColeccion() != null) {
            stats.getProvinciaConMasHechosPorColeccion().forEach((coleccion, provincia) ->
                csv.append(String.format("Provincia por coleccion,%s,%s,%d\n",
                    coleccion,
                    provincia.getProvincia(),
                    provincia.getCantidad())));
        }

        // Provincias por categoría
        if (stats.getProvinciaConMasHechosPorCategoria() != null) {
            stats.getProvinciaConMasHechosPorCategoria().forEach((categoria, provincia) ->
                csv.append(String.format("Provincia por categoria,%s,%s,%d\n",
                    categoria,
                    provincia.getProvincia(),
                    provincia.getCantidad())));
        }

        // Horas pico por categoría
        if (stats.getHorasPicoPorCategoria() != null) {
            stats.getHorasPicoPorCategoria().forEach((categoria, hora) ->
                csv.append(String.format("Hora pico por categoria,%s,%d:00,%d\n",
                    categoria,
                    hora.getHora(),
                    hora.getCantidad())));
        }

        // Total solicitudes spam
        csv.append(String.format("Total solicitudes spam,,,,%d\n", stats.getTotalSolicitudesSpam()));

        return csv.toString();
    }
}

