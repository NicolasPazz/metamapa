package ar.edu.utn.frba.dds.domain.hecho;

import java.time.LocalDate;
import ar.edu.utn.frba.dds.domain.user.Contribuyente;
import java.util.HashSet;
import java.util.Set;
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
  private Contribuyente contribuyente;
  private Set<String> etiquetas = new HashSet<>();

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
    dataHecho.setFechaDeAcontecimiento(fecha);
    dataHecho.setLatitud(latitud);
    dataHecho.setLongitud(longitud);

    this.data = dataHecho;
    this.origen = OrigenHecho.MANUAL;
    this.estado = EstadoHecho.VISIBLE;
    // TODO: asignar contribuyente si no es anonimo
    this.contribuyente = null;
  }

  public boolean esDuplicadoDe(Hecho otroHecho) {
    return this.getTitulo().equalsIgnoreCase(otroHecho.getTitulo());
  }

  public void ocultar() {
    this.estado = EstadoHecho.OCULTO;
  }

  public boolean esVisible() {
    // TODO: verificar que no haya una solicitud de eliminacion aceptada
    return this.estado == EstadoHecho.VISIBLE;
  }

  public void agregarEtiqueta(String etiqueta) {
    if (etiqueta != null && !etiqueta.isBlank()) {
      this.etiquetas.add(etiqueta.trim().toLowerCase());
    }
  }

  public boolean contieneEtiqueta(String etiqueta) {
    return this.etiquetas.contains(etiqueta.trim().toLowerCase());
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

  public LocalDate getFechaDeAcontecimiento() {
    return data.getFechaDeAcontecimiento();
  }

  public LocalDate getFechaDeCarga() {
    return data.getFechaDeCarga();
  }
}