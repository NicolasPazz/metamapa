package ar.utn.ba.ddsi.estatica.repositorie;

import ar.utn.ba.ddsi.estatica.domain.hecho.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IHechosRepository extends JpaRepository<Hecho, Long> {
  List<Hecho> findByEnviadoFalse();
  Optional<Hecho> findFirstByTituloIgnoreCase(String titulo);
}

