package ar.utn.ba.ddsi.agregador.repositorie;

import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IColeccionesRepository extends JpaRepository<Coleccion, UUID> {
    // Método paginado para mejor performance
    Page<Coleccion> findAll(Pageable pageable);
}
