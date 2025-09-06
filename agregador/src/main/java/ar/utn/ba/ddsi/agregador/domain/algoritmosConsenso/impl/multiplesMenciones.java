package ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.impl;

import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;

import java.util.List;
import java.util.Objects;

public class multiplesMenciones extends AAlgoritmoConsenso {

    @Override
    public Boolean esConsensuado(Hecho hecho, int fuentesTotales, List<Hecho> hechos) {
        return this.aparicionesMayorA(hecho, 2, hechos) && !tieneConflictosDeTitulo(hecho, hechos);
    }

}
