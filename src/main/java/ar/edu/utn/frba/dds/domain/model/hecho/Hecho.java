package ar.edu.utn.frba.dds.domain.model.hecho;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Hecho {

  private DataHecho data;
  private OrigenHecho origen;
  private EstadoHecho estado;

  public Hecho(DataHecho data, OrigenHecho origen) {
    this.data = data;
    this.origen = origen;
    this.estado = EstadoHecho.VISIBLE;
  }

  public Hecho(String titulo, String descripcion, String categoria, LocalDate fecha, double latitud, double longitud) {
    DataHecho dataHecho = new DataHecho();
    dataHecho.setTitulo(titulo);
    dataHecho.setDescripcion(descripcion);
    dataHecho.setCategoria(categoria);
    dataHecho.setFecha(fecha);
    dataHecho.setLatitud(latitud);
    dataHecho.setLongitud(longitud);

    this.data = dataHecho;
    this.origen = OrigenHecho.MANUAL;
    this.estado = EstadoHecho.VISIBLE;
  }


  public boolean esDuplicadoDe(Hecho otroHecho) {
    return this.getTitulo().equalsIgnoreCase(otroHecho.getTitulo());
  }

  public void marcarComoOculto() {
    this.estado = EstadoHecho.OCULTO;
  }

  public String getTitulo() {
    return data.getTitulo();
  }

  public String getDescripcion() {
    return data.getDescripcion();
  }

  public String getCategoria() {
    return data.getCategoria();
  }

  public Double getLatitud() {
    return data.getLatitud();
  }

  public Double getLongitud() {
    return data.getLongitud();
  }

  public LocalDate getFecha() {
    return data.getFecha();
  }
}
