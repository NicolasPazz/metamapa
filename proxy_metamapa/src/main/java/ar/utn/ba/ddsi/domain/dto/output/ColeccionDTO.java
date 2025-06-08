package ar.utn.ba.ddsi.domain.dto.output;

import java.util.List;
import lombok.Data;

@Data
public class ColeccionDTO {
  public String handle;
  public String titulo;
  public String descripcion;
  public List<HechoDTO> hechos;
}