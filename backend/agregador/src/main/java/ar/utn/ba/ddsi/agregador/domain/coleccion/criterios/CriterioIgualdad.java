package ar.utn.ba.ddsi.agregador.domain.coleccion.criterios;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CriterioIgualdad implements Criterio {
  @JsonIgnore
  private final TipoCriterio tipo = TipoCriterio.IGUALDAD;
  private String cadenaAComparar;
  private TipoDato tipoDeDato;

  @Override
  public boolean cumple(Hecho hecho) {
    if (cadenaAComparar == null || cadenaAComparar.isBlank()) {
      return true; // Sin restricción, acepta todos
    }

    String busqueda = cadenaAComparar.trim().toLowerCase();

    return switch (tipoDeDato) {
      case CATEGORIA -> hecho.getCategoria() != null &&
                        hecho.getCategoria().getNombre() != null &&
                        hecho.getCategoria().getNombre().trim().toLowerCase().contains(busqueda);
      case DESCRIPCION -> hecho.getDescripcion() != null &&
                          hecho.getDescripcion().trim().toLowerCase().contains(busqueda);
      case ETIQUETA -> hecho.contieneEtiqueta(cadenaAComparar.trim());
      case TITULO -> hecho.getTitulo() != null &&
                     hecho.getTitulo().trim().toLowerCase().contains(busqueda);
      default -> throw new IllegalArgumentException("Tipo de dato no soportado: " + tipoDeDato);
    };
  }
}
