package ar.edu.utn.frba.dds.domain.model.coleccion;

import ar.edu.utn.frba.dds.domain.datasource.Fuente;
import ar.edu.utn.frba.dds.domain.model.coleccion.criterioDePertenencia.Criterio;
import ar.edu.utn.frba.dds.domain.model.hecho.Hecho;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Coleccion {
  private String titulo;
  private String descripcion;
  private Fuente fuente;
  private Criterio criterioDePertenencia;

  public Coleccion(String titulo, String descripcion, Fuente fuente, Criterio criterioDePertenencia) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.fuente = fuente;
    this.criterioDePertenencia = criterioDePertenencia;
  }

  public List<Hecho> importarHechosVisibles() throws Exception {
    return this.fuente.importarHechos().stream().filter(Hecho::esVisible).collect(Collectors.toList());
  }

  public List<Hecho> obtenerHechos() throws Exception {
    List<Hecho> hechosVisibles = this.importarHechosVisibles();

    if (criterioDePertenencia == null) {
      return hechosVisibles;
    }
    // Si hay criterioDePertenencia, lo aplicamos como filtro
    return hechosVisibles.stream()
            .filter(criterioDePertenencia::cumple)
            .toList();
  }
}
