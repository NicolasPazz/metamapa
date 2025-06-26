package ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.impl;

import ar.utn.ba.ddsi.agregador.domain.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;

import java.util.List;
import java.util.Objects;

public class multiplesMenciones extends AAlgoritmoConsenso {
    IHechosRepository hechosRepository;

    @Override
    public Boolean esConsensuado(Hecho hecho) {
        return this.aparicionesMayorA(hecho, 2) && !this.igualTituloDistintosAtributos(hechosRepository.findAll(), hecho);
    }

    private Boolean igualTituloDistintosAtributos(List<Hecho> hechos, Hecho hecho) {
        for(Hecho vHecho : hechos) {
            if (Objects.equals(vHecho.getTitulo(), hecho.getTitulo()) && !Objects.equals(vHecho.getData(), hecho.getData())) {
                return true;
            }
        }
        return false;
    }
}
