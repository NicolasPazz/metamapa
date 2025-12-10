package ar.utn.ba.ddsi.agregador.dto.external;

import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class ColeccionDTO {
  public UUID handle;
  public String titulo;
  public String descripcion;
  public List<HechoDTO> hechos;
}
