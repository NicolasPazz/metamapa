package ar.utn.ba.ddsi.servicio_agregador.domain.repositorie;

import ar.utn.ba.ddsi.servicio_agregador.domain.user.Contribuyente;

public interface IContribuyentesRepository {
  public Contribuyente findById(Long id);
}
