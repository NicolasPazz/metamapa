package ar.utn.ba.ddsi.estadistica.domain.repositorie;

import ar.utn.ba.ddsi.estadistica.domain.hecho.HechosCategoriaProvincia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IHechosCategoriaProvinciaRepository extends JpaRepository<HechosCategoriaProvincia, Long> {
  @Query("""
           select h
           from HechosCategoriaProvincia h
           where h.categoria.nombre = :categoria
           """)
  List<HechosCategoriaProvincia> findByCategoria_Nombre(@Param("categoria") String categoria);
}