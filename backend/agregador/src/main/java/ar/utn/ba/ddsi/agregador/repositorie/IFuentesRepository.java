package ar.utn.ba.ddsi.agregador.repositorie;

import ar.utn.ba.ddsi.agregador.domain.fuente.Fuente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFuentesRepository extends JpaRepository<Fuente, Long> {
    List<Fuente> findByActivaTrue();
}

