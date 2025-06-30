package ar.utn.ba.ddsi.agregador.domain.repositorie.impl;

import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IColeccionesRepository;

import java.util.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class ColeccionesRepository implements IColeccionesRepository {
    private final HechosRepository hechosRepository;
    private final Map<String, Coleccion> colecciones = new HashMap<>();

    @Override
    public Coleccion findByHandle(String handle) {
        return this.colecciones.get(handle);
    }

    @Override
    public Collection<Coleccion> findAll() {
        return colecciones.values();
    }

    @Override
    public void save(Coleccion coleccion) {
        if (colecciones.containsKey(coleccion.getHandle())) {
            throw new IllegalArgumentException("Ya existe una colección con handle: " + coleccion.getHandle());
        }
        colecciones.put(coleccion.getHandle(), coleccion);
    }

    @Override
    public void update(Coleccion coleccion){
        if (!colecciones.containsKey(coleccion.getHandle())) {
            throw new IllegalArgumentException("No existe una colección con handle: " + coleccion.getHandle());
        }
        colecciones.put(coleccion.getHandle(), coleccion);
    }

    @Override
    public void delete(Coleccion coleccion) {
        colecciones.remove(coleccion.getHandle());
    }

    @Override
    public void actualizarColeccion(String handle) {
        Coleccion coleccion = colecciones.get(handle);
        if (coleccion != null) {
            Set<Hecho> hechosFiltrados = hechosRepository.getHechosPorCriterio(
                coleccion.getCriterioDePertenencia()
            );
            coleccion.setHechos(new ArrayList<>(hechosFiltrados));
        }
    }

    @Override
    public void actualizarColecciones() {
        colecciones.keySet().forEach(this::actualizarColeccion);
    }
}