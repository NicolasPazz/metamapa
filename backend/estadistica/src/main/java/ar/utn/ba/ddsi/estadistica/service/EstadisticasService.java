package ar.utn.ba.ddsi.estadistica.service;

import ar.utn.ba.ddsi.estadistica.client.AgregadorClient;
import ar.utn.ba.ddsi.estadistica.domain.estadistica.*;
import ar.utn.ba.ddsi.estadistica.dto.input.*;
import ar.utn.ba.ddsi.estadistica.dto.output.*;
import ar.utn.ba.ddsi.estadistica.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EstadisticasService {

  private final AgregadorClient agregadorClient;
  private final GeolocalizacionService geolocalizacionService;

  private final EstadisticaColeccionProvinciaRepository coleccionProvinciaRepo;
  private final EstadisticaCategoriaDominanteRepository categoriaDominanteRepo;
  private final EstadisticaCategoriaProvinciaRepository categoriaProvinciaRepo;
  private final EstadisticaCategoriaHoraRepository categoriaHoraRepo;
  private final EstadisticaSpamRepository spamRepo;

  @Transactional
  public void recalcularTodasLasEstadisticas() {
    log.info("🔄 [EstadisticasService] ====== INICIANDO RECALCULO DE ESTADÍSTICAS ======");

    try {
      limpiarEstadisticas();

      List<ColeccionDTO> colecciones = agregadorClient.getAllColecciones();
      List<HechoDTO> hechos = agregadorClient.getAllHechos();
      List<SolicitudDTO> solicitudes = agregadorClient.getAllSolicitudes();

      log.info("📊 [EstadisticasService] Datos obtenidos - Colecciones: {}, Hechos: {}, Solicitudes: {}",
          colecciones.size(), hechos.size(), solicitudes.size());

      generarEstadisticasColeccionProvincia(colecciones);
      generarEstadisticaCategoriaDominante(hechos);
      generarEstadisticasCategoriaProvincia(hechos);
      generarEstadisticasCategoriaHora(hechos);
      generarEstadisticaSpam(solicitudes);

      log.info("✅ [EstadisticasService] ====== RECALCULO DE ESTADÍSTICAS COMPLETADO ======");
    } catch (Exception e) {
      log.error("❌ [EstadisticasService] Error al recalcular estadísticas: {}", e.getMessage(), e);
    }
  }

  private void limpiarEstadisticas() {
    log.info("🗑️ [EstadisticasService] Limpiando estadísticas anteriores...");
    coleccionProvinciaRepo.deleteAll();
    categoriaDominanteRepo.deleteAll();
    categoriaProvinciaRepo.deleteAll();
    categoriaHoraRepo.deleteAll();
    spamRepo.deleteAll();
    log.info("✅ [EstadisticasService] Estadísticas anteriores eliminadas");
  }

  private void generarEstadisticasColeccionProvincia(List<ColeccionDTO> colecciones) {
    log.info("📍 [EstadisticasService] Generando estadísticas de colección-provincia...");

    for (ColeccionDTO coleccion : colecciones) {
      if (coleccion.getHechos() == null || coleccion.getHechos().isEmpty()) {
        continue;
      }

      Map<String, Long> hechosPorProvincia = coleccion.getHechos().stream()
          .map(hecho -> geolocalizacionService.obtenerProvincia(hecho.getLatitud(), hecho.getLongitud()))
          .filter(provincia -> !"Desconocida".equals(provincia))
          .collect(Collectors.groupingBy(
              provincia -> provincia,
              Collectors.counting()
          ));

      hechosPorProvincia.entrySet().stream()
          .max(Map.Entry.comparingByValue())
          .ifPresent(entry -> {
            EstadisticaColeccionProvincia estadistica = EstadisticaColeccionProvincia.builder()
                .coleccionHandle(coleccion.getHandle())
                .coleccionTitulo(coleccion.getTitulo())
                .provinciaNombre(entry.getKey())
                .cantidadHechos(entry.getValue())
                .build();
            coleccionProvinciaRepo.save(estadistica);
          });
    }

    log.info("✅ [EstadisticasService] Estadísticas de colección-provincia generadas: {}",
        coleccionProvinciaRepo.count());
  }

  private void generarEstadisticaCategoriaDominante(List<HechoDTO> hechos) {
    log.info("📊 [EstadisticasService] Generando estadística de categoría dominante...");

    if (hechos.isEmpty()) {
      log.warn("⚠️ [EstadisticasService] No hay hechos para calcular categoría dominante");
      return;
    }

    Map<String, Long> hechosPorCategoria = hechos.stream()
        .filter(hecho -> hecho.getCategoria() != null && !hecho.getCategoria().trim().isEmpty())
        .collect(Collectors.groupingBy(
            HechoDTO::getCategoria,
            Collectors.counting()
        ));

    log.debug("📊 [EstadisticasService] Categorías encontradas: {}", hechosPorCategoria.keySet());

    hechosPorCategoria.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .ifPresent(entry -> {
          EstadisticaCategoriaDominante estadistica = EstadisticaCategoriaDominante.builder()
              .categoriaNombre(entry.getKey())
              .cantidadHechos(entry.getValue())
              .build();
          categoriaDominanteRepo.save(estadistica);
          log.info("✅ [EstadisticasService] Categoría dominante: {} con {} hechos",
              entry.getKey(), entry.getValue());
        });
  }

  private void generarEstadisticasCategoriaProvincia(List<HechoDTO> hechos) {
    log.info("📍 [EstadisticasService] Generando estadísticas de categoría-provincia...");

    Map<String, List<HechoDTO>> hechosPorCategoria = hechos.stream()
        .filter(hecho -> hecho.getCategoria() != null && !hecho.getCategoria().trim().isEmpty())
        .collect(Collectors.groupingBy(HechoDTO::getCategoria));

    log.debug("📍 [EstadisticasService] Procesando {} categorías", hechosPorCategoria.size());

    for (Map.Entry<String, List<HechoDTO>> entry : hechosPorCategoria.entrySet()) {
      String categoria = entry.getKey();
      List<HechoDTO> hechosCategoria = entry.getValue();

      Map<String, Long> hechosPorProvincia = hechosCategoria.stream()
          .map(hecho -> geolocalizacionService.obtenerProvincia(hecho.getLatitud(), hecho.getLongitud()))
          .filter(provincia -> !"Desconocida".equals(provincia))
          .collect(Collectors.groupingBy(
              provincia -> provincia,
              Collectors.counting()
          ));

      hechosPorProvincia.entrySet().stream()
          .max(Map.Entry.comparingByValue())
          .ifPresent(provinciaEntry -> {
            EstadisticaCategoriaProvincia estadistica = EstadisticaCategoriaProvincia.builder()
                .categoriaNombre(categoria)
                .provinciaNombre(provinciaEntry.getKey())
                .cantidadHechos(provinciaEntry.getValue())
                .build();
            categoriaProvinciaRepo.save(estadistica);
            log.debug("  ✓ Categoría: {} → Provincia: {} ({} hechos)",
                categoria, provinciaEntry.getKey(), provinciaEntry.getValue());
          });
    }

    log.info("✅ [EstadisticasService] Estadísticas de categoría-provincia generadas: {}",
        categoriaProvinciaRepo.count());
  }

  private void generarEstadisticasCategoriaHora(List<HechoDTO> hechos) {
    log.info("⏰ [EstadisticasService] Generando estadísticas de categoría-hora...");

    Map<String, List<HechoDTO>> hechosPorCategoria = hechos.stream()
        .filter(hecho -> hecho.getCategoria() != null && !hecho.getCategoria().trim().isEmpty())
        .collect(Collectors.groupingBy(HechoDTO::getCategoria));

    log.debug("⏰ [EstadisticasService] Procesando {} categorías", hechosPorCategoria.size());

    for (Map.Entry<String, List<HechoDTO>> entry : hechosPorCategoria.entrySet()) {
      String categoria = entry.getKey();
      List<HechoDTO> hechosCategoria = entry.getValue();

      Map<Integer, Long> hechosPorHora = hechosCategoria.stream()
          .map(this::obtenerHora)
          .filter(hora -> hora != null)
          .collect(Collectors.groupingBy(
              hora -> hora,
              Collectors.counting()
          ));

      hechosPorHora.entrySet().stream()
          .max(Map.Entry.comparingByValue())
          .ifPresent(horaEntry -> {
            EstadisticaCategoriaHora estadistica = EstadisticaCategoriaHora.builder()
                .categoriaNombre(categoria)
                .hora(horaEntry.getKey())
                .cantidadHechos(horaEntry.getValue())
                .build();
            categoriaHoraRepo.save(estadistica);
            log.debug("  ✓ Categoría: {} → Hora: {}h ({} hechos)",
                categoria, horaEntry.getKey(), horaEntry.getValue());
          });
    }

    log.info("✅ [EstadisticasService] Estadísticas de categoría-hora generadas: {}",
        categoriaHoraRepo.count());
  }

  private Integer obtenerHora(HechoDTO hecho) {
    LocalDateTime fecha = hecho.getFechaAcontecimiento() != null
        ? hecho.getFechaAcontecimiento()
        : hecho.getFechaCarga();

    return fecha != null ? fecha.getHour() : null;
  }

  private void generarEstadisticaSpam(List<SolicitudDTO> solicitudes) {
    log.info("🔍 [EstadisticasService] Generando estadística de spam...");
    log.debug("🔍 [EstadisticasService] Total de solicitudes recibidas: {}", solicitudes.size());

    if (!solicitudes.isEmpty() && log.isDebugEnabled()) {
      solicitudes.stream().limit(5).forEach(s ->
          log.debug("  - Solicitud ID: {}, es_spam: {}", s.getId(), s.getSpam())
      );
    }

    long totalSolicitudes = solicitudes.size();
    long solicitudesSpam = solicitudes.stream()
        .filter(solicitud -> Boolean.TRUE.equals(solicitud.getSpam()))
        .count();

    log.debug("🔍 [EstadisticasService] Solicitudes identificadas como spam: {}", solicitudesSpam);

    double porcentajeSpam = totalSolicitudes > 0
        ? (solicitudesSpam * 100.0) / totalSolicitudes
        : 0.0;

    log.debug("🔍 [EstadisticasService] Cálculo: ({} / {}) * 100 = {}%",
        solicitudesSpam, totalSolicitudes, String.format("%.2f", porcentajeSpam));

    EstadisticaSpam estadistica = EstadisticaSpam.builder()
        .totalSolicitudes(totalSolicitudes)
        .solicitudesSpam(solicitudesSpam)
        .porcentajeSpam(porcentajeSpam)
        .build();

    spamRepo.save(estadistica);
    log.info("✅ [EstadisticasService] Estadística de spam generada - Total: {}, Spam: {}, Porcentaje: {}%",
        totalSolicitudes, solicitudesSpam, String.format("%.2f", porcentajeSpam));
  }

  public EstadisticasCompletasDTO obtenerEstadisticasCompletas() {
    log.info("📊 [EstadisticasService] Obteniendo estadísticas completas...");

    return EstadisticasCompletasDTO.builder()
        .distribucionGeograficaColecciones(obtenerDistribucionGeograficaColecciones())
        .categoriaDominante(obtenerCategoriaDominante())
        .concentracionGeograficaCategorias(obtenerConcentracionGeograficaCategorias())
        .distribucionTemporalCategorias(obtenerDistribucionTemporalCategorias())
        .estadisticaSpam(obtenerEstadisticaSpam())
        .build();
  }

  public ColeccionProvinciaDTO obtenerProvinciaConMasHechosPorColeccion(String handle) {
    log.debug("🔍 [EstadisticasService] Buscando estadísticas para handle: {}", handle);

    List<EstadisticaColeccionProvincia> todasLasEstadisticas = coleccionProvinciaRepo.findAll();
    log.debug("📊 [EstadisticasService] Total de estadísticas en BD: {}", todasLasEstadisticas.size());

    todasLasEstadisticas.forEach(e ->
        log.debug("   - Handle disponible: {}", e.getColeccionHandle())
    );

    Optional<EstadisticaColeccionProvincia> exactMatch = todasLasEstadisticas.stream()
        .filter(e -> e.getColeccionHandle().equals(handle))
        .findFirst();

    if (exactMatch.isPresent()) {
      log.info("✅ [EstadisticasService] Encontrado match exacto para handle: {}", handle);
      EstadisticaColeccionProvincia e = exactMatch.get();
      return ColeccionProvinciaDTO.builder()
          .coleccionHandle(e.getColeccionHandle())
          .coleccionTitulo(e.getColeccionTitulo())
          .provinciaNombre(e.getProvinciaNombre())
          .cantidadHechos(e.getCantidadHechos())
          .build();
    }

    Optional<EstadisticaColeccionProvincia> partialMatch = todasLasEstadisticas.stream()
        .filter(e -> e.getColeccionHandle().contains(handle) || handle.contains(e.getColeccionHandle()))
        .findFirst();

    if (partialMatch.isPresent()) {
      log.info("✅ [EstadisticasService] Encontrado match parcial para handle: {}", handle);
      EstadisticaColeccionProvincia e = partialMatch.get();
      return ColeccionProvinciaDTO.builder()
          .coleccionHandle(e.getColeccionHandle())
          .coleccionTitulo(e.getColeccionTitulo())
          .provinciaNombre(e.getProvinciaNombre())
          .cantidadHechos(e.getCantidadHechos())
          .build();
    }

    log.warn("⚠️ [EstadisticasService] No se encontró ningún match para handle: {}", handle);
    return null;
  }

   public List<ColeccionProvinciaDTO> obtenerTodasLasColeccionesConProvinciaDominante() {
    return obtenerDistribucionGeograficaColecciones();
  }

  public CategoriaDominanteDTO obtenerCategoriaConMasHechos() {
    return obtenerCategoriaDominante();
  }

  public CategoriaProvinciaDTO obtenerProvinciaConMasHechosPorCategoria(String categoria) {
    return categoriaProvinciaRepo.findAll().stream()
        .filter(e -> e.getCategoriaNombre().equalsIgnoreCase(categoria))
        .findFirst()
        .map(e -> CategoriaProvinciaDTO.builder()
            .categoriaNombre(e.getCategoriaNombre())
            .provinciaNombre(e.getProvinciaNombre())
            .cantidadHechos(e.getCantidadHechos())
            .build())
        .orElse(null);
  }

  public List<CategoriaProvinciaDTO> obtenerTodasLasCategoriasConProvinciaDominante() {
    return obtenerConcentracionGeograficaCategorias();
  }

  public CategoriaHoraDTO obtenerHoraConMasHechosPorCategoria(String categoria) {
    return categoriaHoraRepo.findAll().stream()
        .filter(e -> e.getCategoriaNombre().equalsIgnoreCase(categoria))
        .findFirst()
        .map(e -> CategoriaHoraDTO.builder()
            .categoriaNombre(e.getCategoriaNombre())
            .hora(e.getHora())
            .cantidadHechos(e.getCantidadHechos())
            .build())
        .orElse(null);
  }

  public List<CategoriaHoraDTO> obtenerTodasLasCategoriasConHoraDominante() {
    return obtenerDistribucionTemporalCategorias();
  }

  public SpamDTO obtenerEstadisticasSpam() {
    return obtenerEstadisticaSpam();
  }

  private List<ColeccionProvinciaDTO> obtenerDistribucionGeograficaColecciones() {
    return coleccionProvinciaRepo.findAll().stream()
        .map(e -> ColeccionProvinciaDTO.builder()
            .coleccionHandle(e.getColeccionHandle())
            .coleccionTitulo(e.getColeccionTitulo())
            .provinciaNombre(e.getProvinciaNombre())
            .cantidadHechos(e.getCantidadHechos())
            .build())
        .collect(Collectors.toList());
  }

  private CategoriaDominanteDTO obtenerCategoriaDominante() {
    return categoriaDominanteRepo.findAll().stream()
        .findFirst()
        .map(e -> CategoriaDominanteDTO.builder()
            .categoriaNombre(e.getCategoriaNombre())
            .cantidadHechos(e.getCantidadHechos())
            .build())
        .orElse(null);
  }

  private List<CategoriaProvinciaDTO> obtenerConcentracionGeograficaCategorias() {
    return categoriaProvinciaRepo.findAll().stream()
        .map(e -> CategoriaProvinciaDTO.builder()
            .categoriaNombre(e.getCategoriaNombre())
            .provinciaNombre(e.getProvinciaNombre())
            .cantidadHechos(e.getCantidadHechos())
            .build())
        .collect(Collectors.toList());
  }

  private List<CategoriaHoraDTO> obtenerDistribucionTemporalCategorias() {
    return categoriaHoraRepo.findAll().stream()
        .map(e -> CategoriaHoraDTO.builder()
            .categoriaNombre(e.getCategoriaNombre())
            .hora(e.getHora())
            .cantidadHechos(e.getCantidadHechos())
            .build())
        .collect(Collectors.toList());
  }

  private SpamDTO obtenerEstadisticaSpam() {
    return spamRepo.findAll().stream()
        .findFirst()
        .map(e -> SpamDTO.builder()
            .totalSolicitudes(e.getTotalSolicitudes())
            .solicitudesSpam(e.getSolicitudesSpam())
            .porcentajeSpam(e.getPorcentajeSpam())
            .build())
        .orElse(SpamDTO.builder()
            .totalSolicitudes(0L)
            .solicitudesSpam(0L)
            .porcentajeSpam(0.0)
            .build());
  }
}
