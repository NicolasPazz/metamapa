package ar.edu.utn.frba.dds.domain.coleccion;

import ar.edu.utn.frba.dds.service.Fuente;
import ar.edu.utn.frba.dds.domain.coleccion.criterioDePertenencia.Criterio;
import ar.edu.utn.frba.dds.domain.hecho.Hecho;

import java.util.List;
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
    return this.fuente.importarHechos().stream()
        .filter(Hecho::esVisible)
        .toList();
  }

  public List<Hecho> obtenerHechos() throws Exception {
    List<Hecho> hechosVisibles = this.importarHechosVisibles();

    if (criterioDePertenencia == null) {
      return hechosVisibles;
    }

    return hechosVisibles.stream()
        .filter(criterioDePertenencia::cumple)
        .toList();
  }
}