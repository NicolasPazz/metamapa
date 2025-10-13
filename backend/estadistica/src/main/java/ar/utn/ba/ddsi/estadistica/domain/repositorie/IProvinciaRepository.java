package ar.utn.ba.ddsi.estadistica.domain.repositorie;

import ar.utn.ba.ddsi.estadistica.domain.Provincia;
import ar.utn.ba.ddsi.estadistica.domain.ProvinciaEnum;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IProvinciaRepository extends JpaRepository<Provincia, Long> {
  @Query("SELECT p FROM Provincia p WHERE p.nombre = :nombre")
  Optional<Provincia> findByNombre(ProvinciaEnum nombre);
}