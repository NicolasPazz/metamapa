package ar.utn.ba.ddsi.servicio_fuente_proxy.domain.repositorie;

import ar.utn.ba.ddsi.servicio_fuente_proxy.domain.hecho.ContenidoMultimedia;
import java.util.List;

public interface IMultimediasRepository {
  public List<ContenidoMultimedia> findAllById(List<Long> idMultimedias);
}
