package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CriterioFuente implements Criterio {
    private final TipoCriterio tipo = TipoCriterio.FUENTE;
    private Set<Long> fuentes = new HashSet<>();

    public CriterioFuente(Long idFuenteExcluida) {
        this.fuentes.add(idFuenteExcluida);
    }

    public void agregarExclusion(Long idFuente) {
        fuentes.add(idFuente);
    }

    public void eliminarExclusion(Long idFuente) {
        fuentes.remove(idFuente);
    }

    @Override
    public boolean cumple(Hecho hecho){
        Long idFuente = hecho.getFuenteId();
        return !fuentes.contains(idFuente);
    }
}