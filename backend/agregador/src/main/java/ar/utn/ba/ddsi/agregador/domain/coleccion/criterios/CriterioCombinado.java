package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CriterioCombinado implements Criterio {
  private TipoCriterio tipo = TipoCriterio.COMBINADO;
  private List<Criterio> criterios = new ArrayList<>();

  public CriterioCombinado(List<Criterio> criterios) {
    this.criterios = criterios;
  }

  public void agregarCriterio(Criterio criterio) {
    if (criterio == null) return;
    if (this.criterios == null) this.criterios = new ArrayList<>();
    this.criterios.add(criterio);
  }

  public void eliminarCriterio(Criterio criterio) {
    if (criterio == null || this.criterios == null) return;
    this.criterios.remove(criterio);
  }
  @Override
  public boolean cumple(Hecho hecho) {
    return criterios.stream().allMatch(criterio -> criterio.cumple(hecho));
  }

}