package ar.utn.ba.ddsi.agregador.repositorie;

import ar.utn.ba.ddsi.agregador.domain.hecho.EstadoHecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IHechosRepository extends JpaRepository<Hecho, Long> {

  @Query("SELECT DISTINCT h FROM Hecho h " +
      "LEFT JOIN FETCH h.provincia " +
      "WHERE " +
      "h.estado = 'VISIBLE' AND " +  // Filtrar solo hechos VISIBLES
      "(:categoria IS NULL OR h.categoria.nombre = :categoria) AND " +
      "(:fechaReporteDesde IS NULL OR h.fechaDeCarga >= :fechaReporteDesde) AND " +
      "(:fechaReporteHasta IS NULL OR h.fechaDeCarga <= :fechaReporteHasta) AND " +
      "(:fechaAcontecimientoDesde IS NULL OR h.fechaDeAcontecimiento >= :fechaAcontecimientoDesde) AND " +
      "(:fechaAcontecimientoHasta IS NULL OR h.fechaDeAcontecimiento <= :fechaAcontecimientoHasta) AND " +
      "(:provincia IS NULL OR h.provincia.nombre = :provincia) AND " +
      "(:fuente IS NULL OR h.nombreFuente = :fuente)")
  List<Hecho> findByFilters(
      @Param("categoria") String categoria,
      @Param("fechaReporteDesde") LocalDate fechaReporteDesde,
      @Param("fechaReporteHasta") LocalDate fechaReporteHasta,
      @Param("fechaAcontecimientoDesde") LocalDateTime fechaAcontecimientoDesde,
      @Param("fechaAcontecimientoHasta") LocalDateTime fechaAcontecimientoHasta,
      @Param("provincia") String provincia,
      @Param("fuente") String fuente
  );

  @Query("SELECT h FROM Hecho h " +
      "LEFT JOIN h.provincia " +
      "WHERE " +
      "h.estado = 'VISIBLE' AND " +
      "(:categoria IS NULL OR h.categoria.nombre = :categoria) AND " +
      "(:fechaReporteDesde IS NULL OR h.fechaDeCarga >= :fechaReporteDesde) AND " +
      "(:fechaReporteHasta IS NULL OR h.fechaDeCarga <= :fechaReporteHasta) AND " +
      "(:fechaAcontecimientoDesde IS NULL OR h.fechaDeAcontecimiento >= :fechaAcontecimientoDesde) AND " +
      "(:fechaAcontecimientoHasta IS NULL OR h.fechaDeAcontecimiento <= :fechaAcontecimientoHasta) AND " +
      "(:provincia IS NULL OR h.provincia.nombre = :provincia) AND " +
      "(:fuente IS NULL OR h.nombreFuente = :fuente)")
  Page<Hecho> findByFiltersPaginated(
      @Param("categoria") String categoria,
      @Param("fechaReporteDesde") LocalDate fechaReporteDesde,
      @Param("fechaReporteHasta") LocalDate fechaReporteHasta,
      @Param("fechaAcontecimientoDesde") LocalDateTime fechaAcontecimientoDesde,
      @Param("fechaAcontecimientoHasta") LocalDateTime fechaAcontecimientoHasta,
      @Param("provincia") String provincia,
      @Param("fuente") String fuente,
      Pageable pageable
  );

  @Query("SELECT COUNT(DISTINCT h) FROM Hecho h " +
      "WHERE " +
      "h.estado = 'VISIBLE' AND " +
      "(:categoria IS NULL OR h.categoria.nombre = :categoria) AND " +
      "(:fechaReporteDesde IS NULL OR h.fechaDeCarga >= :fechaReporteDesde) AND " +
      "(:fechaReporteHasta IS NULL OR h.fechaDeCarga <= :fechaReporteHasta) AND " +
      "(:fechaAcontecimientoDesde IS NULL OR h.fechaDeAcontecimiento >= :fechaAcontecimientoDesde) AND " +
      "(:fechaAcontecimientoHasta IS NULL OR h.fechaDeAcontecimiento <= :fechaAcontecimientoHasta) AND " +
      "(:provincia IS NULL OR h.provincia.nombre = :provincia) AND " +
      "(:fuente IS NULL OR h.nombreFuente = :fuente)")
  long countByFilters(
      @Param("categoria") String categoria,
      @Param("fechaReporteDesde") LocalDate fechaReporteDesde,
      @Param("fechaReporteHasta") LocalDate fechaReporteHasta,
      @Param("fechaAcontecimientoDesde") LocalDateTime fechaAcontecimientoDesde,
      @Param("fechaAcontecimientoHasta") LocalDateTime fechaAcontecimientoHasta,
      @Param("provincia") String provincia,
      @Param("fuente") String fuente
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
                                                LocalDateTime fechaAcontecimientoDesde,
                                                LocalDateTime fechaAcontecimientoHasta,
                                                String ubicacion,
                                                String fuente) {
                                                  
    String provincia = (ubicacion != null && !ubicacion.isEmpty()) ? ubicacion : null;

    return findByFilters(categoria, fechaReporteDesde, fechaReporteHasta,
        fechaAcontecimientoDesde, fechaAcontecimientoHasta, provincia, fuente);
  }

  default Page<Hecho> findByFiltersConUbicacionPaginated(String categoria,
                                                         LocalDate fechaReporteDesde,
                                                         LocalDate fechaReporteHasta,
                                                         LocalDateTime fechaAcontecimientoDesde,
                                                         LocalDateTime fechaAcontecimientoHasta,
                                                         String ubicacion,
                                                         String fuente,
                                                         Pageable pageable) {
    String provincia = (ubicacion != null && !ubicacion.isEmpty()) ? ubicacion : null;

    return findByFiltersPaginated(categoria, fechaReporteDesde, fechaReporteHasta,
        fechaAcontecimientoDesde, fechaAcontecimientoHasta, provincia, fuente, pageable);
  }

  default long countByFiltersConUbicacion(String categoria,
                                         LocalDate fechaReporteDesde,
                                         LocalDate fechaReporteHasta,
                                         LocalDateTime fechaAcontecimientoDesde,
                                         LocalDateTime fechaAcontecimientoHasta,
                                         String ubicacion,
                                         String fuente) {
    String provincia = (ubicacion != null && !ubicacion.isEmpty()) ? ubicacion : null;

    return countByFilters(categoria, fechaReporteDesde, fechaReporteHasta,
        fechaAcontecimientoDesde, fechaAcontecimientoHasta, provincia, fuente);
  }

  @Query("SELECT COUNT(h) > 0 FROM Hecho h " +
         "WHERE h.estado = 'VISIBLE' AND h.nombreFuente = :nombreFuente")
  boolean existsByNombreFuenteAndEstadoVisible(@Param("nombreFuente") String nombreFuente);

  @Query("SELECT h FROM Hecho h WHERE h.estado = :estado")
  List<Hecho> findByEstado(@Param("estado") EstadoHecho estado);
}