package ar.edu.utn.frba.dds.domain.repositorie;

import ar.edu.utn.frba.dds.domain.hecho.Hecho;

public interface IHechosRepository {
  Hecho findById(Long id);
}
