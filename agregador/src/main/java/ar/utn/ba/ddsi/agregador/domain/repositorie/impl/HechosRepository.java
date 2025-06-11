package ar.utn.ba.ddsi.agregador.domain.repositorie.impl;

import ar.utn.ba.ddsi.agregador.domain.hecho.*;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IColeccionesRepository;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HechosRepository implements IHechosRepository {

  private List<Hecho> hechos;
  private final AtomicLong idGenerator;

  private final IColeccionesRepository coleccionesRepository;

  public HechosRepository(IColeccionesRepository coleccionesRepository) {
    this.hechos = new ArrayList<>();
    this.idGenerator = new AtomicLong(1);
    this.coleccionesRepository = coleccionesRepository;
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
  public List<Hecho> findByFilters(String categoria,
                                   LocalDate fechaReporteDesde,
                                   LocalDate fechaReporteHasta,
                                   LocalDate fechaAcontecimientoDesde,
                                   LocalDate fechaAcontecimientoHasta,
                                   String   ubicacion) {

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
            categoria.equalsIgnoreCase(h.getCategoria().getNombre()))

        .filter(h -> fechaReporteDesde == null ||
            !h.getFechaDeCarga()
                .toLocalDate()
                .isAfter(fechaReporteDesde))
        .filter(h -> fechaReporteHasta == null ||
            !h.getFechaDeCarga()
                .toLocalDate()
                .isBefore(fechaReporteHasta))

        .filter(h -> fechaAcontecimientoDesde == null ||
            !h.getFechaDeAcontecimiento()
                .isAfter(fechaAcontecimientoDesde))
        .filter(h -> fechaAcontecimientoHasta == null ||
            !h.getFechaDeAcontecimiento()
                .isBefore(fechaAcontecimientoDesde))

        .filter(h -> lat == null || (
            Double.compare(h.getLatitud(),  lat) == 0 &&
                Double.compare(h.getLongitud(), lon) == 0))

        .toList();
  }

  @Override
  public void save(Hecho hecho) {
    hecho.setId(idGenerator.getAndIncrement());
    this.hechos.add(hecho);
    coleccionesRepository.actualizarColecciones();
  }

  @Override
  public void saveBatch(List<Hecho> nuevosHechos) {
    if (nuevosHechos == null || nuevosHechos.isEmpty()) return;

    for (Hecho hecho : nuevosHechos) {
      hecho.setId(idGenerator.getAndIncrement());
      this.hechos.add(hecho);
    }
    coleccionesRepository.actualizarColecciones();
  }

  @Override
  public void update(Hecho hecho) {
    for (int i = 0; i < hechos.size(); i++) {
      if (hechos.get(i).getId().equals(hecho.getId())) {
        hechos.set(i, hecho);
        return;
      }
    }
  }

  @Override
  public void delete(Hecho hecho) {
    this.hechos.remove(hecho);
  }
}
