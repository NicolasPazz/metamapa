package ar.utn.ba.ddsi.estatica.repositorie;

import ar.utn.ba.ddsi.estatica.domain.Archivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IArchivoRepository extends JpaRepository<Archivo, Long> {
  Optional<Archivo> findByNombre(String nombre);
  List<Archivo> findByProcesadoFalse();
}

