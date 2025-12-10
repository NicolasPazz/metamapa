package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CriterioIgualdad implements Criterio {
  private final TipoCriterio tipo = TipoCriterio.IGUALDAD;
  private String cadenaAComparar;
  private TipoDato tipoDeDato;

  @Override
  public boolean cumple(Hecho hecho) {
    return switch (tipoDeDato) {
      case CATEGORIA -> hecho.getCategoria().getNombre().equalsIgnoreCase(cadenaAComparar.trim());
      case DESCRIPCION -> hecho.getDescripcion().equalsIgnoreCase(cadenaAComparar.trim());
      case ETIQUETA -> hecho.contieneEtiqueta(cadenaAComparar);
      case TITULO -> hecho.getTitulo().equalsIgnoreCase(cadenaAComparar.trim());
      default -> throw new IllegalArgumentException("Tipo de dato no soportado: " + tipoDeDato);
    };
  }
}
