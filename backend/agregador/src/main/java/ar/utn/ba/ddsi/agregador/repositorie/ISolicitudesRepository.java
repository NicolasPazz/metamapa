package ar.utn.ba.ddsi.agregador.repositorie;

import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.EstadoSolicitud;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.SolicitudEliminacion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ISolicitudesRepository extends JpaRepository<SolicitudEliminacion, Long> {
  List<SolicitudEliminacion> findByEstado(EstadoSolicitud estadoSolicitud);
  
  @Query("SELECT s FROM SolicitudEliminacion s LEFT JOIN FETCH s.hecho WHERE s.id = :id")
  Optional<SolicitudEliminacion> findByIdWithHecho(@Param("id") Long id);
}
