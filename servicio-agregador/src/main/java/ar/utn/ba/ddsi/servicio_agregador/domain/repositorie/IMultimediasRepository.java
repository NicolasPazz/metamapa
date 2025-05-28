package ar.utn.ba.ddsi.servicio_agregador.domain.repositorie;

import ar.utn.ba.ddsi.servicio_agregador.domain.hecho.ContenidoMultimedia;
import java.util.List;

public interface IMultimediasRepository {
  public List<ContenidoMultimedia> findAllById(List<Long> idMultimedias);
}
