package ar.utn.ba.ddsi.dinamica.domain.repositorie;

import ar.utn.ba.ddsi.dinamica.domain.hecho.Categoria;

import java.util.List;

public interface ICategoriasRepository {
    public List<Categoria> findAll();
    public Categoria findById(Long id);
    public Categoria findByNombre(String nombre);
    public void save(Categoria categoria);
    public void delete(Categoria categoria);
}
