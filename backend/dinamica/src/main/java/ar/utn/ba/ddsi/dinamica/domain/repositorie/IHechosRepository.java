package ar.utn.ba.ddsi.dinamica.domain.repositorie;

import ar.utn.ba.ddsi.dinamica.domain.hecho.Hecho;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IHechosRepository extends JpaRepository<Hecho, Long> {
  // Devuelve todos los hechos que aún no fueron enviados
  List<Hecho> findByEnviadoFalse();
}