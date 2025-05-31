package ar.utn.ba.ddsi.dinamica.domain.repositorie;

import ar.utn.ba.ddsi.dinamica.domain.hecho.Hecho;

import java.util.List;

public interface IHechosRepository {
    public Hecho findById(Long id);
    public List<Hecho> findAll();
    public void save(Hecho hecho);
    void update(Hecho hecho);
    public void delete(Hecho hecho);
}
