package ar.utn.ba.ddsi.agregador.repositorie;

import ar.utn.ba.ddsi.agregador.domain.hecho.EstadoHecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IHechosRepository extends JpaRepository<Hecho, Long> {

  @Query("SELECT h FROM Hecho h LEFT JOIN FETCH h.categoria")
  List<Hecho> findAllWithCategoria();

  @Query("SELECT h FROM Hecho h WHERE " +
      "h.estado = 'VISIBLE' AND " +  // Filtrar solo hechos VISIBLES
      "(:categoria IS NULL OR h.categoria.nombre = :categoria) AND " +
      "(:fechaReporteDesde IS NULL OR h.fechaDeCarga >= :fechaReporteDesde) AND " +
      "(:fechaReporteHasta IS NULL OR h.fechaDeCarga <= :fechaReporteHasta) AND " +
      "(:fechaAcontecimientoDesde IS NULL OR h.fechaDeAcontecimiento >= :fechaAcontecimientoDesde) AND " +
      "(:fechaAcontecimientoHasta IS NULL OR h.fechaDeAcontecimiento <= :fechaAcontecimientoHasta) AND " +
      "(:latitud IS NULL OR :longitud IS NULL OR (h.latitud = :latitud AND h.longitud = :longitud))")
  List<Hecho> findByFilters(
      @Param("categoria") String categoria,
      @Param("fechaReporteDesde") LocalDate fechaReporteDesde,
      @Param("fechaReporteHasta") LocalDate fechaReporteHasta,
      @Param("fechaAcontecimientoDesde") LocalDate fechaAcontecimientoDesde,
      @Param("fechaAcontecimientoHasta") LocalDate fechaAcontecimientoHasta,
      @Param("latitud") String latitud,
      @Param("longitud") String longitud
  );

  @Modifying
  @Transactional
  @Query("UPDATE Hecho h SET h.estado = :estado WHERE h.id = :idHecho")
  void actualizarEstado(@Param("idHecho") Long idHecho, @Param("estado") EstadoHecho estado);

  @Query("SELECT h FROM Hecho h WHERE h.fuenteId = :fuenteId AND h.idOrigen = :idOrigen")
  Optional<Hecho> findByFuenteIdAndIdOrigen(@Param("fuenteId") Long fuenteId, @Param("idOrigen") Long idOrigen);

  default List<Hecho> findByFiltersConUbicacion(String categoria,
                                                LocalDate fechaReporteDesde,
                                                LocalDate fechaReporteHasta,
                                                LocalDate fechaAcontecimientoDesde,
                                                LocalDate fechaAcontecimientoHasta,
                                                String ubicacion) {
    String latitud = null;
    String longitud = null;
    if (ubicacion != null && ubicacion.contains(",")) {
      String[] parts = ubicacion.split(",");
      latitud = parts[0].trim();
      longitud = parts[1].trim();
    }
    return findByFilters(categoria, fechaReporteDesde, fechaReporteHasta,
        fechaAcontecimientoDesde, fechaAcontecimientoHasta, latitud, longitud);
  }

  @Modifying
  @Transactional
  default void ocultar(Long idHecho) {
    actualizarEstado(idHecho, EstadoHecho.OCULTO);
  }
}