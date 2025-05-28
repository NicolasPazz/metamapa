package ar.utn.ba.ddsi.monomodule.domain.repositorie;

import ar.utn.ba.ddsi.monomodule.domain.user.Contribuyente;

public interface IContribuyentesRepository {
  public Contribuyente findById(Long id);
}
