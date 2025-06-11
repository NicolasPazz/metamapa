package ar.utn.ba.ddsi.agregador.domain.repositorie;

import ar.utn.ba.ddsi.agregador.user.Contribuyente;
import java.util.List;

public interface IContribuyentesRepository {
  public List<Long> findAll();
  public Long findById(Long id);
  public void save(Long contribuyente);
  public void delete(Long contribuyente);
}

