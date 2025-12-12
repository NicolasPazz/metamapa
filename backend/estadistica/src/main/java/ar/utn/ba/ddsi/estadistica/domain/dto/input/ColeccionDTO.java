package ar.utn.ba.ddsi.estadistica.domain.dto.input;

import lombok.Data;

import java.util.List;
import java.util.UUID;
@Data
public class ColeccionDTO {
  private UUID handle;
  private String titulo;
  private List<HechoDTO> hechos;
  private List<HechoDTO> hechosConsensuados;
}