package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.service.impl.IdService;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CriterioFuente implements Criterio {
    private String tipo = "fuente";
    private Set<Long> fuentesExcluidas = new HashSet<>();
    private IdService idService;

    public CriterioFuente(Long idFuenteExcluida, IdService idService) {
        this.fuentesExcluidas.add(idFuenteExcluida);
        this.idService = idService;
    }

    public void agregarExclusion(Long idFuente) {
        fuentesExcluidas.add(idFuente);
    }

    public void eliminarExclusion(Long idFuente) {
        fuentesExcluidas.remove(idFuente);
    }

    @Override
    public boolean cumple(Hecho hecho){
        Long idFuente = idService.getIdFuente(hecho.getId());

        return !fuentesExcluidas.contains(idFuente);
    }
}