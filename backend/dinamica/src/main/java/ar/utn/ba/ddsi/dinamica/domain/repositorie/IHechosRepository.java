package ar.utn.ba.ddsi.dinamica.domain.repositorie;

import ar.utn.ba.ddsi.dinamica.domain.hecho.Hecho;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IHechosRepository extends JpaRepository<Hecho, Long> {
  // Cargar la solicitud y su multimedia para que esté disponible en toOutputDTO
  @EntityGraph(attributePaths = {"solicitud", "solicitud.multimedia"})
  List<Hecho> findByEnviadoFalse();
}