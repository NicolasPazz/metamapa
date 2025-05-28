package ar.utn.ba.ddsi.estadisticas.domain.repositorie;

import ar.utn.ba.ddsi.estadisticas.domain.user.Contribuyente;

public interface IContribuyentesRepository {
  public Contribuyente findById(Long id);
}
