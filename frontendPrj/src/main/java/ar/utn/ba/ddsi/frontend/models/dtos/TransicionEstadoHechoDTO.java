package ar.utn.ba.ddsi.frontend.models.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransicionEstadoHechoDTO {
  private String estadoFinal;
  private LocalDateTime fecha;
}
