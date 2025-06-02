package ar.utn.ba.ddsi.proxy_metamapa.domain.repositorie;

import ar.utn.ba.ddsi.proxy_metamapa.domain.user.Contribuyente;

public interface IContribuyentesRepository {
  public Contribuyente findById(Long id);
}
