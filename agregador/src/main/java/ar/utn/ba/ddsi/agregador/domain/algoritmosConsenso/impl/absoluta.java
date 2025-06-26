package ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.impl;

import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;

import java.util.List;

public class absoluta extends AAlgoritmoConsenso {
    private int fuentesTotales;

    @Override
    protected Boolean esConsensuado(Hecho hecho) {
        return this.aparicionesMayorA(hecho, fuentesTotales);
    }
}
