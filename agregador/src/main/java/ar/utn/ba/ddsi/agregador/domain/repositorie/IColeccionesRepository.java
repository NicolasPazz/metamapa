package ar.utn.ba.ddsi.agregador.domain.repositorie;

import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import java.util.Collection;

public interface IColeccionesRepository {
    public Coleccion findByHandle(String handle);
    public Collection<Coleccion> findAll();
    public void save(Coleccion coleccion);
    public void delete(Coleccion coleccion);
    public void actualizarColecciones();
}
