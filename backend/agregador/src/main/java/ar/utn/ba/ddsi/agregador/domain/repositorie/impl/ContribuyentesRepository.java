package ar.utn.ba.ddsi.agregador.domain.repositorie.impl;

/*
@Repository
public class ContribuyentesRepository implements IContribuyentesRepository {
  private List<Long> contribuyentes;
  //private final AtomicLong idGenerador = new AtomicLong(1);

  public ContribuyentesRepository() {
    this.contribuyentes = new ArrayList<>();
  }

  public Long findById(Long id) {
    return this.contribuyentes.stream()
        .filter(contribuyente -> contribuyente.equals(id))
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<Long> findAll() {
    return this.contribuyentes;
  }

  @Override
  public void save(Long contribuyente) {
    //Long id = idGenerador.getAndIncrement();
    // contribuyente.setId(id);
    contribuyentes.add(contribuyente);
  }

  @Override
  public void delete(Long contribuyente) {
    this.contribuyentes.remove(contribuyente);
  }
}
*/