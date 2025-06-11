package ar.utn.ba.ddsi.proxy_metamapa.domain.dto;

import lombok.Data;

@Data
public class TransicionEstadoHechoDTO {
  private String estadoFinal;
  private String fecha;

  public TransicionEstadoHechoDTO(String estadoFinal, String fecha) {
    this.estadoFinal = estadoFinal;
    this.fecha = fecha;
  }
}
