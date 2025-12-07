package ar.utn.ba.ddsi.agregador.domain.repositorie;

import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IColeccionesRepository extends JpaRepository<Coleccion, String> {
     /*Coleccion findByHandle(String handle);
     //Collection<Coleccion> findAll();
     //void save(Coleccion coleccion);
     void update(Coleccion coleccion);
     void delete(Coleccion coleccion);
     void actualizarColeccion(String handle);
     void actualizarColecciones();*/
}
