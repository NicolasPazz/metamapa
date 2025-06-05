package ar.utn.ba.ddsi.agregador.domain.repositorie;

import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import java.util.Collection;

public interface IColeccionesRepository {
    public Coleccion findById(String id);
    public Collection<Coleccion> findAll();
    public void save(Coleccion coleccion);
    public void delete(Coleccion coleccion);
}
