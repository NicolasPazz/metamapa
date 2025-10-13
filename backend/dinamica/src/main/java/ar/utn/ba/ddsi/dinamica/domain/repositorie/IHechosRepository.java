package ar.utn.ba.ddsi.dinamica.domain.repositorie;

import ar.utn.ba.ddsi.dinamica.domain.hecho.Hecho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IHechosRepository extends JpaRepository<Hecho, Long> {
  /*public Hecho findById(Long id);
  public List<Hecho> findAll();
  public void save(Hecho hecho);
  public void update(Hecho hecho);
  public void delete(Hecho hecho);*/
  @Query("SELECT h FROM Hecho h WHERE h.idSolicitud = :idSolicitud")
  Hecho findByIdSolicitud(@Param("idSolicitud") Long id);
}
