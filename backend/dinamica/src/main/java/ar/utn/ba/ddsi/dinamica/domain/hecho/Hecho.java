package ar.utn.ba.ddsi.dinamica.domain.hecho;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
  private List<String> etiquetas = new ArrayList<>();
  @Builder.Default
  private List<ContenidoMultimedia> multimediaPaths = new ArrayList<>();
  @Builder.Default
  private List<TransicionEstadoHecho> transiciones = new ArrayList<>();
  private LocalDateTime ultimaActualizacion;


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