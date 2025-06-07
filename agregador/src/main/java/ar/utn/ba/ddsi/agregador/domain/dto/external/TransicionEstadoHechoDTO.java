package ar.utn.ba.ddsi.agregador.domain.dto.external;

import lombok.Data;

@Data
public class TransicionEstadoHechoDTO {
  private String estadoFinal;
  private String fecha;
}
