package ar.utn.ba.ddsi.estadistica.domain.repositorie;

import ar.utn.ba.ddsi.estadistica.domain.SolicitudEliminacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ISolicitudRepository extends JpaRepository<SolicitudEliminacion, Long> {
  @Query("select count(s) from SolicitudEliminacion s where s.spam = true")
  long countBySpamTrue();
}