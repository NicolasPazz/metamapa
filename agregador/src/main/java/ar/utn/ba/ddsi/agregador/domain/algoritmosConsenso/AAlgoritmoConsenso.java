package ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;

import java.util.List;

public abstract class AAlgoritmoConsenso {
    private IHechosRepository hechosRepository;

    protected abstract Boolean esConsensuado(Hecho hecho);

    public int contarApariciones(Hecho hecho, List<Hecho> hechos) {
        int cont = 0;

        for(Hecho vHecho : hechos) {
            if(vHecho.equals(hecho)) cont++;
        }

        return cont;
    }

    public Boolean aparicionesMayorA(Hecho hecho, int comparador) {
        List<Hecho> hechosTotales = this.hechosRepository.findAll();
        return this.contarApariciones(hecho, hechosTotales) >= comparador;
    }
}
