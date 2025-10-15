package ar.utn.ba.ddsi.frontend.models.dtos;


import lombok.Data;

import java.util.List;

@Data
public class ColeccionDTO {
  public String handle;
  public String titulo;
  public String descripcion;
  public List<HechoDTO> hechos;
}
