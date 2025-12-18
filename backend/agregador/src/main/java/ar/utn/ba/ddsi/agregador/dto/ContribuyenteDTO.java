package ar.utn.ba.ddsi.agregador.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ContribuyenteDTO {
  private Long id;
  private String nombre;
  private String apellido;
  @JsonProperty("fecha_nacimiento")
  private LocalDate fechaDeNacimiento;
}
