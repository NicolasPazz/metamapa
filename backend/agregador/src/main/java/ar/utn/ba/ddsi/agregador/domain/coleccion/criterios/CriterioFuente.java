package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CriterioFuente implements Criterio {
    @JsonIgnore
    private final TipoCriterio tipo = TipoCriterio.FUENTE;
    private Set<Long> fuentes = new HashSet<>();

    public CriterioFuente(Long idFuente) {
        this.fuentes.add(idFuente);
    }

    public void agregarFuente(Long idFuente) {
        fuentes.add(idFuente);
    }

    public void eliminarFuente(Long idFuente) {
        fuentes.remove(idFuente);
    }

    @Override
    public boolean cumple(Hecho hecho) {
        // Si no hay fuentes especificadas, aceptar todos los hechos
        if (fuentes == null || fuentes.isEmpty()) {
            return true;
        }

        Long idFuente = hecho.getFuenteId();
        // Incluir solo hechos de las fuentes especificadas (lista de INCLUSIÓN)
        return fuentes.contains(idFuente);
    }
}