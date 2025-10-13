package ar.utn.ba.ddsi.estadistica.domain.repositorie;

import ar.utn.ba.ddsi.estadistica.domain.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IColeccionRepository extends JpaRepository<Coleccion, String> {
}