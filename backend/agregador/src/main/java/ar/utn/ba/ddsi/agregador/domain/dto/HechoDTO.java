package ar.utn.ba.ddsi.agregador.domain.dto;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ContenidoMultimediaDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.TransicionEstadoHechoDTO;
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
  private String origen;
  private String estado;
  private ContribuyenteDTO contribuyente;
  private List<String> etiquetas;
  private List<ContenidoMultimediaDTO> multimedia;
  private List<TransicionEstadoHechoDTO> transiciones;
  private String categoria;
}