package ar.utn.ba.ddsi.dinamica.domain.repositorie;

import ar.utn.ba.ddsi.dinamica.domain.hecho.Categoria;
import ar.utn.ba.ddsi.dinamica.domain.user.Contribuyente;

import java.util.List;

public interface IContribuyentesRepository {
    public Contribuyente findById(Long id);
    public List<Contribuyente> findAll();
    public void save(Contribuyente contribuyente);
    public void delete(Contribuyente contribuyente);
}
