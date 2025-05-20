package ar.utn.frba.dds.metamapa.domain.repositorie;

import ar.utn.frba.dds.metamapa.domain.hecho.ContenidoMultimedia;
import java.util.List;

public interface IMultimediasRepository {
  public List<ContenidoMultimedia> findAllById(List<Long> idMultimedias);
}
