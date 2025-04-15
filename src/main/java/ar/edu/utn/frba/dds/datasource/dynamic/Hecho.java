package ar.edu.utn.frba.dds.datasource.dynamic;

import java.time.LocalDateTime;
import java.util.List;
import model.domain.collection.Coleccion;
import model.domain.interfaces.Contribuyente;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Hecho {

  private String titulo;
  private String descripcion;
  private String categoria;
  private Float latitud;
  private Float longitud;
  private LocalDateTime fechaAcontecimiento;
  private LocalDateTime fechaCarga;
  private String origen; //TODO
  private Contribuyente contribuyente;
  private List<Coleccion> colecciones;
  private EstadoHecho estado;

  public Hecho(String titulo, String descripcion, String categoria, Float latitud, Float longitud, LocalDateTime fechaAcontecimiento, LocalDateTime fechaCarga, String origen, Contribuyente contribuyente, List<Coleccion> colecciones, EstadoHecho estado) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaAcontecimiento = fechaAcontecimiento;
    this.fechaCarga = fechaCarga;
    this.origen = origen;
    this.contribuyente = contribuyente;
    this.colecciones = colecciones;
    this.estado = estado;

    //Muchos atributos, verificar
  }

  public boolean esDuplicadoDe(Hecho otroHecho) {
    //TODO
  }

  public void marcarComoOculto() {
    //TODO
  }

}
