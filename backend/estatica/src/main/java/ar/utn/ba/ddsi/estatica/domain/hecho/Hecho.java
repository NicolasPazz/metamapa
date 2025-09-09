package ar.utn.ba.ddsi.estatica.domain.hecho;

import ar.utn.ba.ddsi.estatica.domain.user.Contribuyente;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hecho {
  private Long id;
  private DataHecho data;
  private OrigenHecho origen;
  private EstadoHecho estado;
  private Contribuyente contribuyente;
  @Builder.Default
  private Set<Etiqueta> etiquetas = new HashSet<>();
  @Builder.Default
  private List<ContenidoMultimedia> multimedia = new ArrayList<>();
  @Builder.Default
  private List<TransicionEstadoHecho> transiciones = new ArrayList<>();
  private Categoria categoria;

  // CSV
  public Hecho(DataHecho data, OrigenHecho origen) {
    this.data = data;
    this.origen = origen;
    this.transiciones = new ArrayList<>();
    transicionarEstado(EstadoHecho.VISIBLE);
    // TODO: Los campos que no maneja el CSV como se inicializan?
    this.contribuyente = null;
    this.categoria = null;
    this.etiquetas = new HashSet<>();
    this.multimedia = new ArrayList<>();
  }

  // MANUAL / CONTRIBUYENTE
  public Hecho(String titulo, String descripcion, Categoria categoria, LocalDate fechaDeAcontecimiento, double latitud, double longitud, List<ContenidoMultimedia> multimedia, Set<Etiqueta> etiquetas, Contribuyente contribuyente) {
    DataHecho dataHecho = new DataHecho();
    dataHecho.setTitulo(titulo);
    dataHecho.setDescripcion(descripcion);
    dataHecho.setFechaDeAcontecimiento(fechaDeAcontecimiento);
    dataHecho.setLatitud(latitud);
    dataHecho.setLongitud(longitud);
    dataHecho.setNombreCategoria(categoria.getNombre());

    this.data = dataHecho;
    this.categoria = categoria;
    this.multimedia = multimedia;
    this.etiquetas = etiquetas;
    this.contribuyente = contribuyente;

    if(contribuyente != null) {
      this.origen = OrigenHecho.CONTRIBUYENTE;
    } else {
      this.origen = OrigenHecho.MANUAL;
    }

    transicionarEstado(EstadoHecho.VISIBLE);
  }

  public void transicionarEstado(EstadoHecho estado) {
    this.transiciones.add(new TransicionEstadoHecho(estado, LocalDateTime.now()));
    this.estado = estado;
  }

  public String getTitulo() {
    return data.getTitulo();
  }

  public String getDescripcion() {
    return data.getDescripcion();
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

  public LocalDateTime getFechaDeCarga() {
    return data.getFechaDeCarga();
  }

  public String getNombreCategoria() {
    return data.getNombreCategoria();
  }

  public List<String> getMultimediaPaths() {
    return multimedia.stream()
        .map(ContenidoMultimedia::getPathArchivo)
        .toList();
  }
}