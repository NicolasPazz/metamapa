package ar.utn.ba.ddsi.agregador.domain.repositories;

import ar.utn.ba.ddsi.agregador.user.Contribuyente;
import java.util.List;

public interface IContribuyentesRepository {
  public List<Contribuyente> findAll();
  public Contribuyente findById(Long id);
  public void save(Contribuyente contribuyente);
  public void delete(Contribuyente contribuyente);
}

