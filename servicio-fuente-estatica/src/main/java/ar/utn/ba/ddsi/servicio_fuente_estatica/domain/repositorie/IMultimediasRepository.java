package ar.utn.ba.ddsi.servicio_fuente_estatica.domain.repositorie;

import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.hecho.ContenidoMultimedia;
import java.util.List;

public interface IMultimediasRepository {
  public List<ContenidoMultimedia> findAllById(List<Long> idMultimedias);
}
