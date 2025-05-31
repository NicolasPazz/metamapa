package ar.utn.ba.ddsi.dinamica.service.impl;

import ar.utn.ba.ddsi.dinamica.domain.hecho.Hecho;

import ar.utn.ba.ddsi.dinamica.service.Fuente;
import java.util.List;

public class FuenteDinamica extends Fuente {
    private List<Hecho> hechos;


    public FuenteDinamica(List<Hecho> hechos) {
        this.hechos = hechos;
    }

    @Override
    public List<Hecho> importarHechos() throws Exception{
        return hechos;
    }
    @Override
    public void abrirFuente() throws Exception{

    }
    @Override
    public void cerrarFuente(){

    }
}
