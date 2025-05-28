package ar.utn.ba.ddsi.servicio_estadisticas.domain.repositorie;

import ar.utn.ba.ddsi.servicio_estadisticas.domain.hecho.ContenidoMultimedia;
import java.util.List;

public interface IMultimediasRepository {
  public List<ContenidoMultimedia> findAllById(List<Long> idMultimedias);
}
