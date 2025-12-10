package ar.utn.ba.ddsi.agregador.dto;

import ar.utn.ba.ddsi.agregador.domain.hecho.EstadoHecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.OrigenHecho;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class HechoDTO {
  private Long id;
  private String titulo;
  private String descripcion;
  private Double latitud;
  private Double longitud;
  private LocalDate fechaDeAcontecimiento;
  private LocalDateTime fechaDeCarga;
  private OrigenHecho origen;
  private EstadoHecho estado;
  private Long contribuyenteId;
  private List<String> etiquetas;
  private List<ContenidoMultimediaDTO> multimedia;
  private List<TransicionEstadoHechoDTO> transiciones;
  private String categoria;
}