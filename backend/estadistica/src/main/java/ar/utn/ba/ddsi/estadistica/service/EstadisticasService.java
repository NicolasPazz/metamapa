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

  private final EstadisticaColeccionProvinciaRepository coleccionProvinciaRepo;
  private final EstadisticaCategoriaDominanteRepository categoriaDominanteRepo;
  private final EstadisticaCategoriaProvinciaRepository categoriaProvinciaRepo;
  private final EstadisticaCategoriaHoraRepository categoriaHoraRepo;
  private final EstadisticaSpamRepository spamRepo;

  @Transactional
  public void recalcularTodasLasEstadisticas() {
    try {
      limpiarEstadisticas();
      List<ColeccionDTO> colecciones = agregadorClient.getAllColecciones();
      List<HechoDTO> hechos = agregadorClient.getAllHechos();
      List<SolicitudDTO> solicitudes = agregadorClient.getAllSolicitudes();
      generarEstadisticasColeccionProvincia(colecciones);
      generarEstadisticaCategoriaDominante(hechos);
      generarEstadisticasCategoriaProvincia(hechos);
      generarEstadisticasCategoriaHora(hechos);
      generarEstadisticaSpam(solicitudes);
    } catch (Exception e) {
      log.error("[EstadisticasService] Error al recalcular estadísticas: {}", e.getMessage(), e);
    }
  }

  private void limpiarEstadisticas() {
    coleccionProvinciaRepo.deleteAll();
    categoriaDominanteRepo.deleteAll();
    categoriaProvinciaRepo.deleteAll();
    categoriaHoraRepo.deleteAll();
    spamRepo.deleteAll();
  }

  private void generarEstadisticasColeccionProvincia(List<ColeccionDTO> colecciones) {
    for (ColeccionDTO coleccion : colecciones) {
      if (coleccion.getHechos() == null || coleccion.getHechos().isEmpty()) {
        continue;
      }

      Map<String, Long> hechosPorProvincia = coleccion.getHechos().stream()
          .map(HechoSimpleDTO::getProvincia)
          .filter(provincia -> provincia != null && !"Provincia Desconocida".equals(provincia))
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
  }

  private void generarEstadisticaCategoriaDominante(List<HechoDTO> hechos) {
    if (hechos.isEmpty()) {
      return;
    }

    Map<String, Long> hechosPorCategoria = hechos.stream()
        .filter(hecho -> hecho.getCategoria() != null && !hecho.getCategoria().trim().isEmpty())
        .collect(Collectors.groupingBy(
            HechoDTO::getCategoria,
            Collectors.counting()
        ));

    hechosPorCategoria.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .ifPresent(entry -> {
          EstadisticaCategoriaDominante estadistica = EstadisticaCategoriaDominante.builder()
              .categoriaNombre(entry.getKey())
              .cantidadHechos(entry.getValue())
              .build();
          categoriaDominanteRepo.save(estadistica);
        });
  }

  private void generarEstadisticasCategoriaProvincia(List<HechoDTO> hechos) {
    Map<String, List<HechoDTO>> hechosPorCategoria = hechos.stream()
        .filter(hecho -> hecho.getCategoria() != null && !hecho.getCategoria().trim().isEmpty())
        .collect(Collectors.groupingBy(HechoDTO::getCategoria));

    for (Map.Entry<String, List<HechoDTO>> entry : hechosPorCategoria.entrySet()) {
      String categoria = entry.getKey();
      List<HechoDTO> hechosCategoria = entry.getValue();

      Map<String, Long> hechosPorProvincia = hechosCategoria.stream()
          .map(HechoDTO::getProvincia)
          .filter(provincia -> provincia != null && !"Provincia Desconocida".equals(provincia))
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
          });
    }
  }

  private void generarEstadisticasCategoriaHora(List<HechoDTO> hechos) {
    Map<String, List<HechoDTO>> hechosPorCategoria = hechos.stream()
        .filter(hecho -> hecho.getCategoria() != null && !hecho.getCategoria().trim().isEmpty())
        .collect(Collectors.groupingBy(HechoDTO::getCategoria));

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
          });
    }
  }

  private Integer obtenerHora(HechoDTO hecho) {
    LocalDateTime fecha = hecho.getFechaAcontecimiento() != null
        ? hecho.getFechaAcontecimiento()
        : hecho.getFechaCarga();

    return fecha != null ? fecha.getHour() : null;
  }

  private void generarEstadisticaSpam(List<SolicitudDTO> solicitudes) {
    long totalSolicitudes = solicitudes.size();
    long solicitudesSpam = solicitudes.stream()
        .filter(solicitud -> Boolean.TRUE.equals(solicitud.getSpam()))
        .count();

    double porcentajeSpam = totalSolicitudes > 0
        ? (solicitudesSpam * 100.0) / totalSolicitudes
        : 0.0;

    EstadisticaSpam estadistica = EstadisticaSpam.builder()
        .totalSolicitudes(totalSolicitudes)
        .solicitudesSpam(solicitudesSpam)
        .porcentajeSpam(porcentajeSpam)
        .build();

    spamRepo.save(estadistica);
  }

  public EstadisticasCompletasDTO obtenerEstadisticasCompletas() {

    return EstadisticasCompletasDTO.builder()
        .distribucionGeograficaColecciones(obtenerDistribucionGeograficaColecciones())
        .categoriaDominante(obtenerCategoriaDominante())
        .concentracionGeograficaCategorias(obtenerConcentracionGeograficaCategorias())
        .distribucionTemporalCategorias(obtenerDistribucionTemporalCategorias())
        .estadisticaSpam(obtenerEstadisticaSpam())
        .build();
  }

  public ColeccionProvinciaDTO obtenerProvinciaConMasHechosPorColeccion(String handle) {
    List<EstadisticaColeccionProvincia> todasLasEstadisticas = coleccionProvinciaRepo.findAll();

    Optional<EstadisticaColeccionProvincia> exactMatch = todasLasEstadisticas.stream()
        .filter(e -> e.getColeccionHandle().equals(handle))
        .findFirst();

    if (exactMatch.isPresent()) {
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
      EstadisticaColeccionProvincia e = partialMatch.get();
      return ColeccionProvinciaDTO.builder()
          .coleccionHandle(e.getColeccionHandle())
          .coleccionTitulo(e.getColeccionTitulo())
          .provinciaNombre(e.getProvinciaNombre())
          .cantidadHechos(e.getCantidadHechos())
          .build();
    }

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
