package ar.utn.ba.ddsi.agregador.domain.repositorie;

import ar.utn.ba.ddsi.agregador.domain.user.Contribuyente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IContribuyentesRepository extends JpaRepository<Contribuyente, Long> {
  //public List<Long> findAll();
  //public Long findById(Long id);
  //public void save(Long contribuyente);
  // public void delete(Long contribuyente);
}

