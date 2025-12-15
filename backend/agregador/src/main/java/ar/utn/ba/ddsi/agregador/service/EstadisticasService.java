package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.EstadoSolicitud;
import ar.utn.ba.ddsi.agregador.dto.CategoriaEstadisticaDTO;
import ar.utn.ba.ddsi.agregador.dto.EstadisticasDTO;
import ar.utn.ba.ddsi.agregador.dto.HoraEstadisticaDTO;
import ar.utn.ba.ddsi.agregador.dto.ProvinciaEstadisticaDTO;
import ar.utn.ba.ddsi.agregador.repositorie.IColeccionesRepository;
import ar.utn.ba.ddsi.agregador.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.agregador.repositorie.ISolicitudesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
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
    private final GeocodificacionService geocodificacionService;
    @Value("${estadisticas.base-url:http://localhost:8085/api/estadisticas}")
    private String estadisticasBaseUrl;
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
            estadisticasCache = calcularEstadisticas();
            ultimaActualizacion = LocalDateTime.now();
        }
        return estadisticasCache;
    }

    public void notificarRecalculo() {
        try {
            log.info("[EstadisticasService] Notificando recálculo al microservicio de Estadísticas...");

            WebClient webClient = WebClient.builder()
                .baseUrl(estadisticasBaseUrl)
                .build();

            webClient.post()
                .uri("/recalcular")
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(5))
                .doOnSuccess(response ->
                    log.info("✅ [EstadisticasService] Notificación de recálculo exitosa"))
                .doOnError(ex ->
                    log.warn("⚠️ [EstadisticasService] No se pudo notificar al microservicio de Estadísticas (puede no estar disponible): {}",
                        ex.getMessage()))
                .onErrorComplete()
                .subscribe();

        } catch (Exception e) {
            log.warn("⚠️ [EstadisticasService] Error al intentar notificar recálculo: {}", e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public CategoriaEstadisticaDTO obtenerCategoriaConMasHechos() {
        log.info("[EstadisticasService] Calculando categoría con más hechos");

        List<Hecho> hechos = hechosRepository.findAllWithCategoria().stream()
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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public ProvinciaEstadisticaDTO obtenerProvinciaConMasHechosPorCategoria(String categoria) {
        log.info("[EstadisticasService] Calculando provincia con más hechos para categoría: {}", categoria);

        List<Hecho> hechos = hechosRepository.findAllWithCategoria().stream()
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

    @Transactional(readOnly = true)
    public Map<String, ProvinciaEstadisticaDTO> obtenerProvinciaPorTodasLasCategorias() {
        log.info("[EstadisticasService] Calculando provincia con más hechos para todas las categorías");

        List<Hecho> hechos = hechosRepository.findAllWithCategoria().stream()
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

    @Transactional(readOnly = true)
    public HoraEstadisticaDTO obtenerHoraPicoPorCategoria(String categoria) {
        log.info("[EstadisticasService] Calculando hora pico para categoría: {}", categoria);

        List<Hecho> hechos = hechosRepository.findAllWithCategoria().stream()
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

    @Transactional(readOnly = true)
    public Map<String, HoraEstadisticaDTO> obtenerHoraPicoParaTodasLasCategorias() {
        log.info("[EstadisticasService] Calculando hora pico para todas las categorías");

        List<Hecho> hechos = hechosRepository.findAllWithCategoria().stream()
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
            .filter(s -> s.getEstado() == EstadoSolicitud.RECHAZADA && s.esSpam())
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
                ultimaActualizacion != null ? ultimaActualizacion : LocalDateTime.now()
            )
            .build();
    }


    private String obtenerProvincia(Hecho hecho) {
        return geocodificacionService.obtenerProvinciaPorCoordenadas(
            hecho.getLatitud(),
            hecho.getLongitud()
        );
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

