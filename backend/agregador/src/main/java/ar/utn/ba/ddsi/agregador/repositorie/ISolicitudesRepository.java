package ar.utn.ba.ddsi.agregador.repositorie;

import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.SolicitudEliminacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISolicitudesRepository extends JpaRepository<SolicitudEliminacion, Long> {
}
