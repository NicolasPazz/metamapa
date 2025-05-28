package ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.repositorie.impl;

import ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.hecho.ContenidoMultimedia;
import ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.repositorie.IMultimediasRepository;
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