package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.repositorie.IColeccionesRepository;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.agregador.service.ISeederService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeederService implements ISeederService {
    @Autowired
    private IHechosRepository hechosRepository;
    @Autowired
    private IColeccionesRepository coleccionesRepository;

    @Override
    public void inicializar() {

    }
}
