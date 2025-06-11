package ar.utn.ba.ddsi.agregador.domain.dto.external;

import java.time.LocalDate;
import lombok.Data;

@Data
public class ContribuyenteDTO {
  private String nombre;
  private String apellido;
  private LocalDate fechaDeNacimiento;
}
