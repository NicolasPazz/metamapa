package ar.utn.ba.ddsi.dinamica.domain.repositorie.impl;

/*@Repository
public class ContribuyentesRepository implements IContribuyentesRepository {
    private List<Contribuyente> contribuyentes;
    private final AtomicLong idGenerador = new AtomicLong(1);

    public ContribuyentesRepository() {
        this.contribuyentes = new ArrayList<>();
    }

    @Override
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
    public void save(Contribuyente contribuyente) {//nueva etiqueta
        Long id = idGenerador.getAndIncrement();
        contribuyente.setId(id);
        contribuyentes.add(contribuyente);
    }

    @Override
    public void delete(Contribuyente contribuyente) {
        this.contribuyentes.remove(contribuyente);
    }
}*/
