package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
@AllArgsConstructor
public class CriterioIgualdad implements Criterio {
  private final String cadenaAComparar;
  private final TipoDato tipoDeDato;

  @Override
  public boolean cumple(Hecho hecho) {
    switch (tipoDeDato) {
      case CATEGORIA:
        return hecho.getCategoria().getNombre().equalsIgnoreCase(cadenaAComparar.trim());
      case DESCRIPCION:
        return hecho.getDescripcion().equalsIgnoreCase(cadenaAComparar.trim());
      case ETIQUETA:
        return hecho.contieneEtiqueta(cadenaAComparar.trim());
      case TITULO:
        return hecho.getTitulo().equalsIgnoreCase(cadenaAComparar.trim());
      default:
        throw new IllegalArgumentException("Tipo de dato no soportado: " + tipoDeDato);
    }
  }
}
