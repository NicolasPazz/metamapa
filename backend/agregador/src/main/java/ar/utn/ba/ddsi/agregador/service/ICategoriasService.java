package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.util.List;

public interface ICategoriasService {
  void persistirCategoriasDesdeHechos(List<Hecho> hechosNuevos);
}
