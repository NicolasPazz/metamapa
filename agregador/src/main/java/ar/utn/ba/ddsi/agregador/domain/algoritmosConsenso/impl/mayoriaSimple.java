package ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.impl;

import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.agregador.service.impl.FuentesService;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.List;

public class mayoriaSimple extends AAlgoritmoConsenso {

    @Override
    public Boolean esConsensuado(Hecho hecho, int fuentesTotales, List<Hecho> hechos) {
        return this.aparicionesMayorA(hecho, fuentesTotales / 2, hechos);
    }
}
