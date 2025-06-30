package ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.impl;

import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.agregador.service.impl.FuentesService;

import java.util.List;

public class mayoriaSimple extends AAlgoritmoConsenso {
    private FuentesService fuentesService;
    private final int fuentesTotales = this.fuentesService.cantidadDeFuentes();

    @Override
    public Boolean esConsensuado(Hecho hecho) {
        return this.aparicionesMayorA(hecho, fuentesTotales / 2);
    }
}
