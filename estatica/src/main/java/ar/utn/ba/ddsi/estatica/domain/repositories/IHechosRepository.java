package ar.utn.ba.ddsi.estatica.domain.repositories;

import ar.utn.ba.ddsi.estatica.domain.hecho.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface IHechosRepository extends JpaRepository<Hecho, Long> {
  /*Hecho findById(Long id);
  List<Hecho> findAll();
  void save(Hecho hecho);
  void update(Hecho hecho);
  void delete(Hecho hecho);*/
}
