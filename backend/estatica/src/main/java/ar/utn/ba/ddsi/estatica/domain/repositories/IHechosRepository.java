package ar.utn.ba.ddsi.estatica.domain.repositories;

import ar.utn.ba.ddsi.estatica.domain.hecho.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IHechosRepository extends JpaRepository<Hecho, Long> {
  // Devuelve todos los hechos que aún no fueron enviados
  List<Hecho> findByEnviadoFalse();

  // Busca un hecho por título (ignorando mayúsculas/minúsculas)
  Optional<Hecho> findFirstByTituloIgnoreCase(String titulo);
}
