package ar.utn.ba.ddsi.agregador.domain.repositorie;

import ar.utn.ba.ddsi.agregador.domain.solicitud.SolicitudDeEliminacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISolicitudesRepository extends JpaRepository<SolicitudDeEliminacion, Long> {
  //void save(SolicitudDeEliminacion solicitudDeEliminacion);
  //public List<SolicitudDeEliminacion> findAll();
  //SolicitudDeEliminacion findById(Long idSolicitud);
  //public void eliminar(Long id);
}
