package ar.utn.ba.ddsi.estadistica.repository;

import ar.utn.ba.ddsi.estadistica.domain.estadistica.EstadisticaCategoriaDominante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadisticaCategoriaDominanteRepository extends JpaRepository<EstadisticaCategoriaDominante, Long> {
}

