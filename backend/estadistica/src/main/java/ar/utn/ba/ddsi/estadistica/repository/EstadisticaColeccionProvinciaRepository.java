package ar.utn.ba.ddsi.estadistica.repository;

import ar.utn.ba.ddsi.estadistica.domain.estadistica.EstadisticaColeccionProvincia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadisticaColeccionProvinciaRepository extends JpaRepository<EstadisticaColeccionProvincia, Long> {
}

