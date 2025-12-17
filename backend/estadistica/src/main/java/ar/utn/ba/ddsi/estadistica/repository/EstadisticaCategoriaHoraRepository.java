package ar.utn.ba.ddsi.estadistica.repository;

import ar.utn.ba.ddsi.estadistica.domain.estadistica.EstadisticaCategoriaHora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadisticaCategoriaHoraRepository extends JpaRepository<EstadisticaCategoriaHora, Long> {
}

