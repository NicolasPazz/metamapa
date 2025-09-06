package ar.utn.ba.ddsi.agregador.domain.repositorie.impl;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.Criterio;
import ar.utn.ba.ddsi.agregador.domain.hecho.*;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IColeccionesRepository;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IContribuyentesRepository;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class HechosRepository implements IHechosRepository {

  private final List<Hecho> hechos = new ArrayList<>();
  private final Map<Long, Hecho> hechosOcultos = new HashMap<>();
  private final IColeccionesRepository coleccionesRepository;
  private final IContribuyentesRepository contribuyentesRepository;

  public HechosRepository(@Lazy IColeccionesRepository coleccionesRepository,
                          IContribuyentesRepository contribuyentesRepository) {
    this.coleccionesRepository = coleccionesRepository;
    this.contribuyentesRepository = contribuyentesRepository;
  }

  @Override
  public Hecho findById(Long id) {
    return this.hechos.stream()
        .filter(h -> h.getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<Hecho> findAll() {
    return this.hechos;
  }

  @Override
  public List<Hecho> findByFilters(
      String categoria,
      LocalDate fechaReporteDesde, LocalDate fechaReporteHasta,
      LocalDate fechaAcontecimientoDesde, LocalDate fechaAcontecimientoHasta,
      String ubicacion) {

    Double lat, lon;
    if (ubicacion != null && ubicacion.contains(",")) {
      String[] parts = ubicacion.split(",");
      lat = Double.parseDouble(parts[0]);
      lon = Double.parseDouble(parts[1]);
    } else {
      lat = null;
      lon = null;
    }

    return hechos.stream()
        .filter(h -> categoria == null ||
            (h.getCategoria() != null &&
                h.getCategoria().getNombre() != null &&
                h.getCategoria().getNombre().toLowerCase().contains(categoria.toLowerCase())))

        .filter(h -> fechaReporteDesde == null ||
            !h.getFechaDeCarga()
                .toLocalDate()
                .isBefore(fechaReporteDesde))
        .filter(h -> fechaReporteHasta == null ||
            !h.getFechaDeCarga()
                .toLocalDate()
                .isAfter(fechaReporteHasta))

        .filter(h -> fechaAcontecimientoDesde == null ||
            !h.getFechaDeAcontecimiento()
                .isBefore(fechaAcontecimientoDesde))
        .filter(h -> fechaAcontecimientoHasta == null ||
            !h.getFechaDeAcontecimiento()
                .isAfter(fechaAcontecimientoHasta))

        .filter(h -> lat == null || (
            Double.compare(h.getLatitud(),  lat) == 0 &&
                Double.compare(h.getLongitud(), lon) == 0))

        .toList();
  }

  @Override
  public void save(Hecho hecho) {
    if (isOculto(hecho.getId())) {
      log.warn("[HechosRepository] Hecho {} no fue guardado porque está oculto.", hecho.getId());
      return;
    }

    if (hecho.getContribuyente() != null) {
      Long cid = hecho.getContribuyente();
      if (contribuyentesRepository.findById(cid) == null) {
        contribuyentesRepository.save(cid);
      }
    }

    this.hechos.add(hecho);
    coleccionesRepository.actualizarColecciones();
  }

  @Override
  public void saveBatch(List<Hecho> nuevosHechos) {
    if (nuevosHechos == null || nuevosHechos.isEmpty()) {
      log.info("[HechosRepository] No se recibieron hechos para guardar.");
      return;
    }

    List<Hecho> filtrados = nuevosHechos.stream()
        .filter(h -> !isOculto(h.getId()))
        .toList();

    hechos.addAll(filtrados);
    log.info("[HechosRepository] Se guardaron {} hechos nuevos ({} fueron descartados por estar ocultos).",
        filtrados.size(), nuevosHechos.size() - filtrados.size());

    coleccionesRepository.actualizarColecciones();
  }

  @Override
  public void update(Hecho hecho) {
    for (int i = 0; i < hechos.size(); i++) {
      if (hechos.get(i).getId().equals(hecho.getId())) {
        hechos.set(i, hecho);
        coleccionesRepository.actualizarColecciones();
        return;
      }
    }
  }

  @Override
  public void delete(Hecho hecho) {
    this.hechos.remove(hecho);
    coleccionesRepository.actualizarColecciones();
  }

  @Override
  public void ocultar(Long id)  {
    Hecho hecho = findById(id);
    if (hecho != null) {
      hecho.transicionarEstado(EstadoHecho.OCULTO);
      this.delete(hecho);
      hechosOcultos.put(id, hecho);
      log.info("[HechosRepository] Hecho {} ocultado.", id);
    } else {
      log.warn("[HechosRepository] No se encontró el hecho con ID {} para ocultar.", id);
    }
  }

  @Override
  public Boolean isOculto(Long id) {
    return hechosOcultos.containsKey(id);
  }

  @Override
  public Set<Hecho> getHechosPorCriterio(Criterio criterio) {
    return hechos.stream()
        .filter(criterio::cumple)
        .collect(Collectors.toSet());
  }
}
