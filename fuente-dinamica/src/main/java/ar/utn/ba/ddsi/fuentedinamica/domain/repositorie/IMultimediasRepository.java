package ar.utn.ba.ddsi.fuentedinamica.domain.repositorie;

import ar.utn.ba.ddsi.fuentedinamica.domain.hecho.ContenidoMultimedia;
import java.util.List;

public interface IMultimediasRepository {
  public List<ContenidoMultimedia> findAllById(List<Long> idMultimedias);
}
