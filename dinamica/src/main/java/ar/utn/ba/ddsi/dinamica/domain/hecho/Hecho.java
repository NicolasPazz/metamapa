package ar.utn.ba.ddsi.dinamica.domain.hecho;

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
  private Long idSolicitud;
  private DataHecho data;
  private OrigenHecho origen;
  private EstadoHecho estado;
  private Long idContribuyente;
  @Builder.Default
  private Set<Etiqueta> etiquetas = new HashSet<>();
  @Builder.Default
  private List<String> multimediaPaths = new ArrayList<>();
  @Builder.Default
  private List<TransicionEstadoHecho> transiciones = new ArrayList<>();
  private Categoria categoria;
  private LocalDateTime ultimaActualizacion;

  // CSV
  public Hecho(DataHecho data, OrigenHecho origen) {
    this.data = data;
    this.origen = origen;
    this.transiciones = new ArrayList<>();
    transicionarEstado(EstadoHecho.VISIBLE);
    // TODO: Los campos que no maneja el CSV como se inicializan?
    this.idContribuyente = null;
    this.categoria = null;
    this.etiquetas = new HashSet<>();
    this.multimediaPaths = new ArrayList<>();
  }

  // MANUAL / CONTRIBUYENTE
  public Hecho(String titulo, String descripcion, Categoria categoria, LocalDate fechaDeAcontecimiento, Double latitud, Double longitud, List<String> multimedia, Set<Etiqueta> etiquetas, Long idContribuyente) {
    DataHecho dataHecho = new DataHecho();
    dataHecho.setTitulo(titulo);
    dataHecho.setDescripcion(descripcion);
    dataHecho.setFechaDeAcontecimiento(fechaDeAcontecimiento);
    dataHecho.setLatitud(latitud);
    dataHecho.setLongitud(longitud);
    dataHecho.setNombreCategoria(categoria.getNombre());

    this.data = dataHecho;
    this.categoria = categoria;
    this.multimediaPaths = multimedia;
    this.etiquetas = etiquetas;
    this.idContribuyente = idContribuyente;

    if(idContribuyente != null) {
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

  /* public List<String> getMultimediaPaths() {
    return this.multimediaPaths.stream()
      .map(ContenidoMultimedia::getPathArchivo)
      .toList();
  } */
}