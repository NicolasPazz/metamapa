package ar.utn.ba.ddsi.servicio_estadisticas.domain.repositorie;

import ar.utn.ba.ddsi.servicio_estadisticas.domain.user.Contribuyente;

public interface IContribuyentesRepository {
  public Contribuyente findById(Long id);
}
