package ar.utn.ba.ddsi.dinamica.domain.repositorie.impl;

import ar.utn.ba.ddsi.dinamica.domain.hecho.ContenidoMultimedia;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.IMultimediasRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MultimediasRepository implements IMultimediasRepository {
    private List<ContenidoMultimedia> multimedias;
    private final AtomicLong idGenerador = new AtomicLong(1);

    public MultimediasRepository(List<ContenidoMultimedia> multimedias) {
        this.multimedias = new ArrayList<>();
    }

    @Override
    public ContenidoMultimedia findById(Long id) {
        return this.multimedias.stream()
                .filter(cm -> cm.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<ContenidoMultimedia> findAllById(List<Long> idMultimedias) {
        if (idMultimedias == null || idMultimedias.isEmpty()) return List.of();

        return this.multimedias.stream()
                .filter(m -> idMultimedias.contains(m.getId()))
                .toList();
    }

    @Override
    public List<ContenidoMultimedia> findAll(){
        return this.multimedias;
    }

    public void save(ContenidoMultimedia multimedia){
        Long id = idGenerador.getAndIncrement();
        multimedia.setId(id);
        this.multimedias.add(multimedia);
    }

    public void delete(ContenidoMultimedia multimedia){
        this.multimedias.remove(multimedia);
    }
}
