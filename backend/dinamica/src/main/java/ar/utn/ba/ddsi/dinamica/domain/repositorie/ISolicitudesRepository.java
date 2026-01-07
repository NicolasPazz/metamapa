package ar.utn.ba.ddsi.dinamica.domain.repositorie;

import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ISolicitudesRepository extends JpaRepository<SolicitudHecho, Long> {
  @EntityGraph(attributePaths = "multimedia")
  List<SolicitudHecho> findAll();
  
  @EntityGraph(attributePaths = "multimedia")
  List<SolicitudHecho> findByIdContribuyente(Long idContribuyente);
}
