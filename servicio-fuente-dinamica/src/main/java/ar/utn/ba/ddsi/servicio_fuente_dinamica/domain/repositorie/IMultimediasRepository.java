package ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.repositorie;

import ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.hecho.ContenidoMultimedia;
import java.util.List;

public interface IMultimediasRepository {
  public List<ContenidoMultimedia> findAllById(List<Long> idMultimedias);
}
