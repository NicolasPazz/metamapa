package ar.utn.ba.ddsi.estadistica.domain.repositorie;

import ar.utn.ba.ddsi.estadistica.domain.hecho.HechosColeccionProvincia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IHechosColeccionProvinciaRepository extends JpaRepository<HechosColeccionProvincia, Long> {
  @Query("""
           select h
           from HechosColeccionProvincia h
           where h.coleccion.handle = :handle
           """)
  List<HechosColeccionProvincia> findByColeccion_Handle(@Param("handle") String handle);
}