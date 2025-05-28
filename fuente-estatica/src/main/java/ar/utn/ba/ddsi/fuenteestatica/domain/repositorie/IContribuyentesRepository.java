package ar.utn.ba.ddsi.fuenteestatica.domain.repositorie;

import ar.utn.ba.ddsi.fuenteestatica.domain.user.Contribuyente;

public interface IContribuyentesRepository {
  public Contribuyente findById(Long id);
}
