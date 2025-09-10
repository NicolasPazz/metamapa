package ar.utn.ba.ddsi.agregador.domain.repositorie;

import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.Criterio;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface IHechosRepository extends JpaRepository<Hecho, Long> {
  /* Hecho findById(Long id);
   //List<Hecho> findAll();
  List<Hecho> findByFilters(String categoria,
                            LocalDate fechaReporteDesde,
                            LocalDate fechaReporteHasta,
                            LocalDate fechaAcontecimientoDesde,
                            LocalDate fechaAcontecimientoHasta,
                            String   ubicacion);
   //void save(Hecho hecho);
   void saveBatch(List<Hecho> nuevosHechos);
  void update(Hecho hecho);
   void delete(Hecho hecho);
   void ocultar(Long id);
   Boolean isOculto(Long id);
   Set<Hecho> getHechosPorCriterio(Criterio criterio);*/
    @Query("SELECT h FROM Hecho h WHERE " +
           "(:categoria IS NULL OR h.categoria.nombre = :categoria) AND " +
           "(:fechaReporteDesde IS NULL OR h.data.fechaDeCarga >= :fechaReporteDesde) AND " +
           "(:fechaReporteHasta IS NULL OR h.data.fechaDeCarga <= :fechaReporteHasta) AND " +
           "(:fechaAcontecimientoDesde IS NULL OR h.data.fechaDeAcontecimiento >= :fechaAcontecimientoDesde) AND " +
           "(:fechaAcontecimientoHasta IS NULL OR h.data.fechaDeAcontecimiento <= :fechaAcontecimientoHasta)")
    List<Hecho>findByFilters(@Param("categoria") String categoria,
                             @Param("fechaReporteDesde") LocalDate fechaReporteDesde,
                             @Param("fechaReporteHasta") LocalDate fechaReporteHasta,
                             @Param("fechaAcontecimientoDesde") LocalDate fechaAcontecimientoDesde,
                             @Param("fechaAcontecimientoHasta") LocalDate fechaAcontecimientoHasta,
                             @Param("ubicacion") String ubicacion); //TODO: filtro de ubicacion

    @Modifying
    @Query("UPDATE Hecho h SET h.estado = ar.utn.ba.ddsi.agregador.domain.hecho.EstadoHecho.OCULTO WHERE h.id = :idHecho")
    void ocultar(@Param("idHecho") Long idHecho);
}

