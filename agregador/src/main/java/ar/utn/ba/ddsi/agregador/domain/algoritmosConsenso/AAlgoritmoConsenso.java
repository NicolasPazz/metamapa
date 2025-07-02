package ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;

import java.util.List;

public abstract class AAlgoritmoConsenso {

    public abstract Boolean esConsensuado(Hecho hecho, int fuentesTotales, List<Hecho> hechos);

    public int contarApariciones(Hecho hecho, List<Hecho> hechos) {
        int cont = 0;

        for(Hecho vHecho : hechos) {
            if(vHecho.getId() == hecho.getId()) cont++;
        }

        return cont;
    }

    public Boolean aparicionesMayorA(Hecho hecho, int comparador, List<Hecho> hechos) {
        return this.contarApariciones(hecho, hechos) >= comparador;
    }
}
