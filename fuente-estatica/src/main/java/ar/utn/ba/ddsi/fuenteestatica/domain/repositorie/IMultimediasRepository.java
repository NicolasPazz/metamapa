package ar.utn.ba.ddsi.fuenteestatica.domain.repositorie;

import ar.utn.ba.ddsi.fuenteestatica.domain.hecho.ContenidoMultimedia;
import java.util.List;

public interface IMultimediasRepository {
  public List<ContenidoMultimedia> findAllById(List<Long> idMultimedias);
}
