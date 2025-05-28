package ar.utn.ba.ddsi.monomodule.service.impl;

import ar.utn.ba.ddsi.monomodule.domain.hecho.Hecho;

import ar.utn.ba.ddsi.monomodule.service.IFuenteService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FuenteDinamicaService implements IFuenteService {
    private List<Hecho> hechos;


    public FuenteDinamicaService(List<Hecho> hechos) {
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
