package ar.utn.ba.ddsi.servicio_fuente_proxy.domain.repositorie;

import ar.utn.ba.ddsi.servicio_fuente_proxy.domain.user.Contribuyente;

public interface IContribuyentesRepository {
  public Contribuyente findById(Long id);
}
