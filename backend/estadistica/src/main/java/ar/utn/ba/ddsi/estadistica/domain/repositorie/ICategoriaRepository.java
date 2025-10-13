package ar.utn.ba.ddsi.estadistica.domain.repositorie;

import ar.utn.ba.ddsi.estadistica.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoriaRepository extends JpaRepository<Categoria, Long> {
}