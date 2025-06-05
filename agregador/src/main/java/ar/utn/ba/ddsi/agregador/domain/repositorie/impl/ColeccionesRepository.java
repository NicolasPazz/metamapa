package ar.utn.ba.ddsi.agregador.domain.repositorie.impl;

import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IColeccionesRepository;

import java.util.*;

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
        if(coleccion.getHandle() == null) {
            coleccion.setHandle(Integer.toString(this.colecciones.size()));
            colecciones.put(coleccion.getHandle(), coleccion);
        }
        else {
            colecciones.put(coleccion.getHandle(), coleccion);
        }
    }

    @Override
    public void delete(Coleccion coleccion) {
        colecciones.remove(coleccion.getHandle());
    }
}