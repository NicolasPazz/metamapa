package ar.utn.frba.dds.metamapa.domain.repositorie;

import ar.utn.frba.dds.metamapa.domain.user.Contribuyente;

public interface IContribuyentesRepository {
  public Contribuyente findById(Long id);
}
