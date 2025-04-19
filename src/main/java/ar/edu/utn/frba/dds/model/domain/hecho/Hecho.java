package ar.edu.utn.frba.dds.model.domain.hecho;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Hecho {
  private String titulo;
  private String descripcion;
  private Categoria categoria;
  private Double latitud;
  private Double longitud;
  private LocalDateTime fecha;
  private OrigenHecho origen;
  private EstadoHecho estado;

  public Hecho(String titulo, String descripcion, Categoria categoria, Double latitud, Double longitud, LocalDateTime fecha, OrigenHecho origen) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fecha = fecha;
    this.origen = origen;
    this.estado = EstadoHecho.VISIBLE;
    // Muchos atributos --> verificar --> Code smells
  }

  public boolean esDuplicadoDe(Hecho otroHecho) {
    // TODO
    return false;
  }

  public void marcarComoOculto() {
    this.estado = EstadoHecho.OCULTO;
  }
}
