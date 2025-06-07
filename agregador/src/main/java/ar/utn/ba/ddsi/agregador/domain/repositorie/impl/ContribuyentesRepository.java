package ar.utn.ba.ddsi.agregador.domain.repositorie.impl;

import ar.utn.ba.ddsi.agregador.domain.repositorie.IContribuyentesRepository;
import ar.utn.ba.ddsi.agregador.user.Contribuyente;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ContribuyentesRepository implements IContribuyentesRepository {
  private List<Contribuyente> contribuyentes;
  private final AtomicLong idGenerador = new AtomicLong(1);

  public ContribuyentesRepository() {
    this.contribuyentes = new ArrayList<>();
  }

  public Contribuyente findById(Long id) {
    return this.contribuyentes.stream()
        .filter(contribuyente -> contribuyente.getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<Contribuyente> findAll() {
    return this.contribuyentes;
  }

  @Override
  public void save(Contribuyente contribuyente) {
    Long id = idGenerador.getAndIncrement();
    contribuyente.setId(id);
    contribuyentes.add(contribuyente);
  }

  @Override
  public void delete(Contribuyente contribuyente) {
    this.contribuyentes.remove(contribuyente);
  }
}
