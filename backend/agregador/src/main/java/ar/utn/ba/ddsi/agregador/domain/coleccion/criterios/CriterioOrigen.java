package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CriterioOrigen implements Criterio{
  @JsonIgnore
  private final TipoCriterio tipo = TipoCriterio.ORIGEN;
  private String nombreFuente;

  public CriterioOrigen(String nombreFuente) {
    this.nombreFuente = nombreFuente;
  }

  public boolean cumple(Hecho hecho) {
    if (this.nombreFuente == null) {
      return hecho.getNombreFuente() == null;
    }
    return this.nombreFuente.equals(hecho.getNombreFuente());
  }
}