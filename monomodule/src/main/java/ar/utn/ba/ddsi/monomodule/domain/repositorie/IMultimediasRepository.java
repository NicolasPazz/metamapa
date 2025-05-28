package ar.utn.ba.ddsi.monomodule.domain.repositorie;

import ar.utn.ba.ddsi.monomodule.domain.hecho.ContenidoMultimedia;
import java.util.List;

public interface IMultimediasRepository {
  public List<ContenidoMultimedia> findAllById(List<Long> idMultimedias);
}
