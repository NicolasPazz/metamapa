package ar.utn.ba.ddsi.servicio_fuente_estatica.domain.repositorie;

import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.user.Contribuyente;

public interface IContribuyentesRepository {
  public Contribuyente findById(Long id);
}
