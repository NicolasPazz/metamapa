package ar.utn.ba.ddsi.agregador.repositorie;

import ar.utn.ba.ddsi.agregador.domain.hecho.Categoria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoriaRepository extends JpaRepository<Categoria, Long> {
  List<Categoria> findByNombreIn(List<String> nombres);
}