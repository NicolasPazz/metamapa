package ar.utn.ba.ddsi.proxy_metamapa.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class ContribuyenteDTO {
  private String nombre;
  private String apellido;
  @JsonProperty("fecha_nacimiento")
  private LocalDate fechaDeNacimiento;
}
