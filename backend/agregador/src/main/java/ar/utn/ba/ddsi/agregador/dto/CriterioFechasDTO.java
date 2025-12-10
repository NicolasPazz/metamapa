package ar.utn.ba.ddsi.agregador.dto;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.TipoFecha;
import java.time.LocalDate;
import lombok.Data;

@Data
public class CriterioFechasDTO implements CriterioDTO {
  private LocalDate fechaInicial;
  private LocalDate fechaFinal;
  private TipoFecha tipoFecha;
}
