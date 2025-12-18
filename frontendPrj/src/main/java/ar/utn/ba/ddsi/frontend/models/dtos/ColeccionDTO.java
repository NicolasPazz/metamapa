package ar.utn.ba.ddsi.frontend.models.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ColeccionDTO {
  public String handle;
  public String titulo;
  public String descripcion;
  @JsonProperty("fecha_carga")
  public LocalDateTime fechaDeCarga;
  public List<HechoDTO> hechos;
}
