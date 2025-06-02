package ar.utn.ba.ddsi.proxy_metamapa.domain.repositorie.impl;

import ar.utn.ba.ddsi.proxy_metamapa.domain.hecho.Etiqueta;
import ar.utn.ba.ddsi.proxy_metamapa.domain.repositorie.IEtiquetasRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class EtiquetasRepository implements IEtiquetasRepository {
  private List<Etiqueta> etiquetas;

  public EtiquetasRepository() {
    this.etiquetas = new ArrayList<>();
  }

  @Override
  public Etiqueta findById(Long id) {
    return this.etiquetas.stream()
        .filter(e -> e.getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  @Override
  public Set<Etiqueta> findAllById(List<Long> idEtiquetas) {
    if (idEtiquetas == null || idEtiquetas.isEmpty()) return Set.of();

    return this.etiquetas.stream()
        .filter(e -> idEtiquetas.contains(e.getId()))
        .collect(Collectors.toSet());
  }

  @Override
  public List<Etiqueta> findAll() {
    return this.etiquetas;
  }

  @Override
  public void save(Etiqueta etiqueta) {
    this.etiquetas.add(etiqueta);
    etiqueta.setId((long) this.etiquetas.size());
  }

  @Override
  public void delete(Etiqueta etiqueta) {
    this.etiquetas.remove(etiqueta);
  }
}
