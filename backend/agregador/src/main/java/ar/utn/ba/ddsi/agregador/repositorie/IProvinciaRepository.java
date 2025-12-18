package ar.utn.ba.ddsi.agregador.repositorie;

import ar.utn.ba.ddsi.agregador.domain.ubicacion.Provincia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IProvinciaRepository extends JpaRepository<Provincia, Long> {

    Optional<Provincia> findByNombre(String nombre);

    @Query("SELECT DISTINCT p.nombre FROM Provincia p " +
           "WHERE p.id IN (SELECT DISTINCT h.provincia.id FROM Hecho h WHERE h.estado = 'VISIBLE')")
    List<String> findProvinciasConHechos();
}

