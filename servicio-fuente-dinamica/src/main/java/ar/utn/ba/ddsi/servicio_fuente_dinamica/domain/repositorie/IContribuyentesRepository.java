package ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.repositorie;

import ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.user.Contribuyente;

public interface IContribuyentesRepository {
  public Contribuyente findById(Long id);
}
