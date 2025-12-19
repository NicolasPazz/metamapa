package ar.utn.ba.ddsi.dinamica.domain.repositorie;

import ar.utn.ba.ddsi.dinamica.domain.hecho.Hecho;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IHechosRepository extends JpaRepository<Hecho, Long> {
  // Cargar la solicitud y su multimedia para que esté disponible en toOutputDTO
  @EntityGraph(attributePaths = {"solicitud", "solicitud.multimedia"})
  List<Hecho> findByEnviadoFalse();
  
  // Buscar hecho por ID de solicitud
  Optional<Hecho> findBySolicitud_Id(Long solicitudId);
}