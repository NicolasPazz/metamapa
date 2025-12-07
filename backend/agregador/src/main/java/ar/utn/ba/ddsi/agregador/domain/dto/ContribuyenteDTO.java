package ar.utn.ba.ddsi.agregador.domain.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ContribuyenteDTO {
  private Long id;
  private String nombre;
  private String apellido;
  private LocalDate fechaDeNacimiento;
}
