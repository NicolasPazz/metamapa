package ar.utn.ba.ddsi.agregador.domain.repositorie;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.Criterio;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface IHechosRepository {
   Hecho findById(Long id);
   List<Hecho> findAll();
  List<Hecho> findByFilters(String categoria,
                            LocalDate fechaReporteDesde,
                            LocalDate fechaReporteHasta,
                            LocalDate fechaAcontecimientoDesde,
                            LocalDate fechaAcontecimientoHasta,
                            String   ubicacion);
   void save(Hecho hecho);
   void saveBatch(List<Hecho> nuevosHechos);
  void update(Hecho hecho);
   void delete(Hecho hecho);
   void ocultar(Long id);
   Boolean isOculto(Long id);
   Set<Hecho> getHechosPorCriterio(Criterio criterio);
}

