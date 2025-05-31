package ar.utn.ba.ddsi.dinamica.domain.repositorie.impl;

import ar.utn.ba.ddsi.dinamica.domain.hecho.Etiqueta;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.IEtiquetasRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class EtiquetasRepository implements IEtiquetasRepository {

    private List<Etiqueta> etiquetas;
    private final AtomicLong idGenerador = new AtomicLong(1);

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
        //nueva etiqueta
        Long id = idGenerador.getAndIncrement();
        etiqueta.setId(id);
        etiquetas.add(etiqueta);
    }

    @Override
    public void delete(Etiqueta etiqueta) {
        this.etiquetas.remove(etiqueta);
    }
}
