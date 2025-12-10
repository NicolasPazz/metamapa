package ar.utn.ba.ddsi.agregador.domain.dto.external;

import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
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
