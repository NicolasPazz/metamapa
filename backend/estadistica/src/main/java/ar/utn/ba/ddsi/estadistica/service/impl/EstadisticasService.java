package ar.utn.ba.ddsi.estadistica.service.impl;

import ar.utn.ba.ddsi.estadistica.domain.HechosCategoriaHora;
import ar.utn.ba.ddsi.estadistica.domain.HechosCategoriaProvincia;
import ar.utn.ba.ddsi.estadistica.domain.HechosColeccionProvincia;
import ar.utn.ba.ddsi.estadistica.domain.dto.output.CategoriaEstadisticaDTO;
import ar.utn.ba.ddsi.estadistica.domain.dto.output.HoraEstadisticaDTO;
import ar.utn.ba.ddsi.estadistica.domain.dto.output.ProvinciaPorCategoriaDTO;
import ar.utn.ba.ddsi.estadistica.domain.dto.output.ProvinciaPorColeccionDTO;
import ar.utn.ba.ddsi.estadistica.domain.dto.output.SpamEstadisticaDTO;
import ar.utn.ba.ddsi.estadistica.domain.repositorie.IHechosCategoriaHoraRepository;
import ar.utn.ba.ddsi.estadistica.domain.repositorie.IHechosCategoriaProvinciaRepository;
import ar.utn.ba.ddsi.estadistica.domain.repositorie.IHechosColeccionProvinciaRepository;
import ar.utn.ba.ddsi.estadistica.domain.repositorie.ISolicitudRepository;
import ar.utn.ba.ddsi.estadistica.service.IColeccionService;
import ar.utn.ba.ddsi.estadistica.service.IEstadisticasService;
import ar.utn.ba.ddsi.estadistica.service.ISolicitudService;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstadisticasService implements IEstadisticasService {

  private final IColeccionService coleccionService;
  private final ISolicitudService solicitudService;
  private final IHechosCategoriaHoraRepository hechosCategoriaHoraRepository;
  private final IHechosCategoriaProvinciaRepository hechosCategoriaProvinciaRepository;
  private final IHechosColeccionProvinciaRepository hechosColeccionProvinciaRepository;
  private final ISolicitudRepository solicitudRepository;

  @Override
  @Transactional
  public void refrescoEstadisticas() {
    try {
      coleccionService.refrescarColecciones();
      solicitudService.refrescarSolicitudes();
      log.info("Refresco de estadísticas completado exitosamente");
    } catch (Exception e) {
      log.error("Error durante el recálculo de estadísticas: {}", e.getMessage(), e);
    }
  }

  @Override
  @Transactional(Transactional.TxType.SUPPORTS)
  public ProvinciaPorColeccionDTO calcularProvinciaMasHechosPorColeccion(String handle) {
    log.info("Calculando provincia con más hechos para la colección: {}", handle);

    var filas = hechosColeccionProvinciaRepository.findByColeccion_Handle(handle);
    if (filas == null || filas.isEmpty()) {
      return ProvinciaPorColeccionDTO.builder()
          .coleccion(handle)
          .provincia("Sin datos")
          .cantidadHechos(0L)
          .build();
    }

    HechosColeccionProvincia top = filas.stream()
        .max(Comparator.comparingLong(HechosColeccionProvincia::getCantidadHechos))
        .orElseThrow();

    return ProvinciaPorColeccionDTO.builder()
        .coleccion(handle)
        .provincia(top.getProvincia().getNombre().name()) // Enum -> texto
        .cantidadHechos(top.getCantidadHechos())
        .build();
  }

  @Override
  @Transactional(Transactional.TxType.SUPPORTS)
  public CategoriaEstadisticaDTO calcularCategoriaMasHechos() {
    log.info("Calculando categoría con más hechos…");

    Map<String, Long> totalesPorCategoria = hechosCategoriaProvinciaRepository.findAll().stream()
        .collect(Collectors.groupingBy(
            hcp -> hcp.getCategoria().getNombre(),
            Collectors.summingLong(HechosCategoriaProvincia::getCantidadHechos)
        ));

    if (totalesPorCategoria.isEmpty()) {
      return CategoriaEstadisticaDTO.builder()
          .categoria("Sin datos")
          .cantidadHechos(0L)
          .build();
    }

    var top = totalesPorCategoria.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .orElseThrow();

    return CategoriaEstadisticaDTO.builder()
        .categoria(top.getKey())
        .cantidadHechos(top.getValue())
        .build();
  }

  @Override
  @Transactional(Transactional.TxType.SUPPORTS)
  public ProvinciaPorCategoriaDTO calcularProvinciaMasHechosPorCategoria(String categoria) {
    log.info("Calculando provincia con más hechos para la categoría: {}", categoria);

    var filas = hechosCategoriaProvinciaRepository.findByCategoria_Nombre(categoria);
    if (filas == null || filas.isEmpty()) {
      return ProvinciaPorCategoriaDTO.builder()
          .categoria(categoria)
          .provincia("Sin datos")
          .cantidadHechos(0L)
          .build();
    }

    HechosCategoriaProvincia top = filas.stream()
        .max(Comparator.comparingLong(HechosCategoriaProvincia::getCantidadHechos))
        .orElseThrow();

    return ProvinciaPorCategoriaDTO.builder()
        .categoria(categoria)
        .provincia(top.getProvincia().getNombre().name())
        .cantidadHechos(top.getCantidadHechos())
        .build();
  }

  @Override
  @Transactional(Transactional.TxType.SUPPORTS)
  public HoraEstadisticaDTO calcularHoraMasHechosPorCategoria(String categoria) {
    log.info("Calculando hora con más hechos para la categoría: {}", categoria);

    var filas = hechosCategoriaHoraRepository.findByCategoria_Nombre(categoria);
    if (filas == null || filas.isEmpty()) {
      return HoraEstadisticaDTO.builder()
          .categoria(categoria)
          .hora("00:00")
          .cantidadHechos(0L)
          .build();
    }

    HechosCategoriaHora top = filas.stream()
        .max(Comparator.comparingLong(HechosCategoriaHora::getCantidadHechos))
        .orElseThrow();

    String horaStr = String.format("%02d:00", top.getHora());
    return HoraEstadisticaDTO.builder()
        .categoria(categoria)
        .hora(horaStr)
        .cantidadHechos(top.getCantidadHechos())
        .build();
  }

  @Override
  @Transactional(Transactional.TxType.SUPPORTS)
  public SpamEstadisticaDTO calcularSolicitudesSpam() {
    log.info("Calculando estadísticas de spam desde BD…");

    long total = solicitudRepository.count();
    long spam = solicitudRepository.countBySpamTrue();   // asumimos existe
    long validas = total - spam;
    double porcentajeSpam = total > 0 ? (double) spam * 100.0 / total : 0.0;

    log.info("Total solicitudes: {}, Spam: {}, Válidas: {}, Porcentaje spam: {}",
        total, spam, validas, String.format(java.util.Locale.US, "%.2f%%", porcentajeSpam));

    return SpamEstadisticaDTO.builder()
        .totalSolicitudes(total)
        .solicitudesSpam(spam)
        .solicitudesValidas(validas)
        .porcentajeSpam(porcentajeSpam)
        .build();
  }
}
