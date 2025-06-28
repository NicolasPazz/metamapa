package ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.impl;

import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;

public class absoluta extends AAlgoritmoConsenso {
    private int fuentesTotales;

    @Override
    public Boolean esConsensuado(Hecho hecho) {
        return this.aparicionesMayorA(hecho, fuentesTotales);
    }
}
