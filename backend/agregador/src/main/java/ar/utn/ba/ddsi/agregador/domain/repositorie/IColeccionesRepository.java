package ar.utn.ba.ddsi.agregador.domain.repositorie;

import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import java.util.Collection;

public interface IColeccionesRepository {
     Coleccion findByHandle(String handle);
     Collection<Coleccion> findAll();
     void save(Coleccion coleccion);
     void update(Coleccion coleccion);
     void delete(Coleccion coleccion);
     void actualizarColeccion(String handle);
     void actualizarColecciones();
}
