package ar.utn.ba.ddsi.estadisticas.domain.repositorie.impl;

import ar.utn.ba.ddsi.estadisticas.domain.hecho.ContenidoMultimedia;
import ar.utn.ba.ddsi.estadisticas.domain.repositorie.IMultimediasRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class MultimediasRepository implements IMultimediasRepository {
  private List<ContenidoMultimedia> multimedias;

  @Override
  public List<ContenidoMultimedia> findAllById(List<Long> idMultimedias) {
    if (idMultimedias == null || idMultimedias.isEmpty()) return List.of();

    return this.multimedias.stream()
        .filter(m -> idMultimedias.contains(m.getId()))
        .toList();
  }
}