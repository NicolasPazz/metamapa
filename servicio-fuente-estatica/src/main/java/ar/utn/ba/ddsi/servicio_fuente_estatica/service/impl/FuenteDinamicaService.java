package ar.utn.ba.ddsi.servicio_fuente_estatica.service.impl;

import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.hecho.Hecho;

import ar.utn.ba.ddsi.servicio_fuente_estatica.service.IFuenteService;
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
