package ar.utn.ba.ddsi.estadistica.domain.repositorie;

import ar.utn.ba.ddsi.estadistica.domain.HechosCategoriaHora;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IHechosCategoriaHoraRepository extends JpaRepository<HechosCategoriaHora, Long> {
  @Query("""
           select h
           from HechosCategoriaHora h
           where h.categoria.nombre = :categoria
           """)
  List<HechosCategoriaHora> findByCategoria_Nombre(@Param("categoria") String categoria);
}