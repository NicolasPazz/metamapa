package ar.utn.ba.ddsi.fuenteproxy.domain.repositorie;

import ar.utn.ba.ddsi.fuenteproxy.domain.user.Contribuyente;

public interface IContribuyentesRepository {
  public Contribuyente findById(Long id);
}
