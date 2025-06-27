package ar.utn.ba.ddsi.agregador.domain.repositorie;

import ar.utn.ba.ddsi.agregador.domain.hecho.EstadoHecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.time.LocalDate;
import java.util.List;

public interface IHechosRepository {
  public Hecho findById(Long id);
  public List<Hecho> findAll();
  List<Hecho> findByFilters(String categoria,
                            LocalDate fechaReporteDesde,
                            LocalDate fechaReporteHasta,
                            LocalDate fechaAcontecimientoDesde,
                            LocalDate fechaAcontecimientoHasta,
                            String   ubicacion);
  public void save(Hecho hecho);
  public void saveBatch(List<Hecho> nuevosHechos);
  void update(Hecho hecho);
  public void delete(Hecho hecho);
  public void ocultar(Long id);
  public Boolean isOculto(Long id);
}

