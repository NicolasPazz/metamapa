package ar.utn.ba.ddsi.fuenteestatica.domain.repositorie.impl;

import ar.utn.ba.ddsi.fuenteestatica.domain.repositorie.IContribuyentesRepository;
import ar.utn.ba.ddsi.fuenteestatica.domain.user.Contribuyente;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

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
