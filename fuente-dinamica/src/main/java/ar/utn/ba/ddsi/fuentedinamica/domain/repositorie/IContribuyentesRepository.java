package ar.utn.ba.ddsi.fuentedinamica.domain.repositorie;

import ar.utn.ba.ddsi.fuentedinamica.domain.user.Contribuyente;

public interface IContribuyentesRepository {
  public Contribuyente findById(Long id);
}
