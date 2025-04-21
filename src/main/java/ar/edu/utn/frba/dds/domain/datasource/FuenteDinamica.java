package ar.edu.utn.frba.dds.domain.datasource;

import ar.edu.utn.frba.dds.domain.model.hecho.Hecho;

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
