package ar.utn.ba.ddsi.agregador.domain.dto;

import java.util.List;
import lombok.Data;

@Data
public class HechoDTO {
  private Long id;
  private String origen;
  private String estado;
  private ContribuyenteDTO contribuyente;
  private List<String> etiquetas;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private String fechaDeAcontecimiento;
  private String fechaDeCarga;
  private String ultimaModificacion;
  private List<ContenidoMultimediaDTO> contenidoMultimedia;
  private List<TransicionEstadoHechoDTO> transicionesDeEstado;
}