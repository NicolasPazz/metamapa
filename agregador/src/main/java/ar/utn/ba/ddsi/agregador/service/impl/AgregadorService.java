package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.repositories.IHechosRepository;
import ar.utn.ba.ddsi.agregador.service.IAgregadorService;
import ar.utn.ba.ddsi.agregador.service.IColeccionesService;

public class AgregadorService implements IAgregadorService {
    private HechosService hechosService;
    private IColeccionesService coleccionesService;
    private IHechosRepository hechosRepository;

    @Override
    public void actualizarHechos() {
        this.hechosService.guardarHechos();
    }

    public void actualizarColecciones() {
        coleccionesService.actualizarColecciones(this.hechosRepository.findAll());
    }
}
