package ar.utn.ba.ddsi.estadistica.repository;

import ar.utn.ba.ddsi.estadistica.domain.provincia.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProvinciaRepository extends JpaRepository<Provincia, Long> {
  Optional<Provincia> findByNombre(String nombre);
}

