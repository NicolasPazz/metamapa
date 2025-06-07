package ar.utn.ba.ddsi.agregador.domain.repositorie.impl;

import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IColeccionesRepository;

import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class ColeccionesRepository implements IColeccionesRepository {
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
    public void delete(Coleccion coleccion) {
        colecciones.remove(coleccion.getHandle());
    }

    @Override
    public void actualizarColecciones() {
        for (Coleccion coleccion : colecciones.values()) {
            coleccion.actualizar();
        }
    }
}