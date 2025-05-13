package ar.edu.utn.frba.dds.domain.repositorie;

import ar.edu.utn.frba.dds.domain.user.Contribuyente;

public interface IContribuyentesRepository {
  Contribuyente findById(Long id);
}
