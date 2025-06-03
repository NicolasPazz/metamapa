package ar.utn.ba.ddsi.agregador.domain.coleccion;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterioDePertenencia.Criterio;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Coleccion {
  private String handle;
  private String titulo;
  private String descripcion;
  private List<Hecho> hechos;
  private Criterio criterioDePertenencia;

  public Coleccion(String titulo, String descripcion, List<Hecho> hechos, Criterio criterioDePertenencia) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.hechos = hechos;
    this.criterioDePertenencia = criterioDePertenencia;
  }

  /*public List<Hecho> importarHechosVisibles() throws Exception {
    return this.fuente.importarHechos().stream()
        .filter(Hecho::esVisible)
        .toList();
  }*/

  public List<Hecho> obtenerHechos() throws Exception {

    if (criterioDePertenencia == null) {
      return this.getHechos();
    }

    return this.hechos.stream()
        .filter(criterioDePertenencia::cumple)
        .toList();
  }
}