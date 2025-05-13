package ar.edu.utn.frba.dds.domain.repositorie.impl;

import ar.edu.utn.frba.dds.domain.repositorie.IContribuyentesRepository;
import ar.edu.utn.frba.dds.domain.user.Contribuyente;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ContribuyenteRepository implements IContribuyentesRepository {
  private final List<Contribuyente> contribuyentes = new ArrayList<>();

  @Override
  public Contribuyente findById(Long id) {
    return this.contribuyentes.stream()
        .filter(contribuyente -> contribuyente.getId().equals(id))
        .findFirst()
        .orElse(null);
  }
}
