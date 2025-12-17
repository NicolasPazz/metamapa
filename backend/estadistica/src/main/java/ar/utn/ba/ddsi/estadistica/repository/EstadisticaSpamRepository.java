package ar.utn.ba.ddsi.estadistica.repository;

import ar.utn.ba.ddsi.estadistica.domain.estadistica.EstadisticaSpam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadisticaSpamRepository extends JpaRepository<EstadisticaSpam, Long> {
}

