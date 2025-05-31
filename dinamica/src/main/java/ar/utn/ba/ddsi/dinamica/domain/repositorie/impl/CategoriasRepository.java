package ar.utn.ba.ddsi.dinamica.domain.repositorie.impl;

import ar.utn.ba.ddsi.dinamica.domain.hecho.Categoria;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.ICategoriasRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CategoriasRepository implements ICategoriasRepository {

    private List<Categoria> categorias;
    private final AtomicLong idGenerador = new AtomicLong(1);


    public CategoriasRepository() {
        this.categorias = new ArrayList<>();
    }

    @Override
    public List<Categoria> findAll() {
        return this.categorias;
    }

    @Override
    public Categoria findById(Long id) {
        return this.categorias.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Categoria findByNombre(String nombre) {
        return this.categorias.stream()
                .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElse(null);
    }


    @Override
    public void save(Categoria categoria) {
        //nueva categoria
        Long id = idGenerador.getAndIncrement();
        categoria.setId(id);
        categorias.add(categoria);
    }

    @Override
    public void delete(Categoria categoria) {
        this.categorias.remove(categoria);
    }
}
