package model.domain.collection;

import ar.edu.utn.frba.dds.datasource.FuenteDeDato;
import ar.edu.utn.frba.dds.datasource.dynamic.Hecho;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Coleccion {

  private String titulo;
  private String descripcion;
  private FuenteDeDato fuente;
  private Criterio criterioDePertenencia;

  public Coleccion(String titulo, String descripcion, FuenteDeDato fuente, Criterio criterioDePertenencia) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.fuente = fuente;
    this.criterioDePertenencia = criterioDePertenencia;
  }

  public List<Hecho> obtenerHechos() {
    //TODO
  }

}
