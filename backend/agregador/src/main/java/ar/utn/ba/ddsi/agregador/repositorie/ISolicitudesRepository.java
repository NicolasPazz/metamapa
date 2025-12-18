package ar.utn.ba.ddsi.agregador.repositorie;

import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.EstadoSolicitud;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.SolicitudEliminacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISolicitudesRepository extends JpaRepository<SolicitudEliminacion, Long> {
  List<SolicitudEliminacion> findByEstado(EstadoSolicitud estadoSolicitud);
}
