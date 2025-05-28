package ar.utn.ba.ddsi.fuentedinamica.domain.dto.output;

import ar.utn.ba.ddsi.fuentedinamica.domain.request.EstadoSolicitud;
import java.time.LocalDate;
import lombok.Data;

@Data
public class SolicitudOutputDTO {
  private Long id;
  private Long idHecho;
  private String textoFundamentos;
  private LocalDate fecha;
  private EstadoSolicitud estado;
  private Long idContribuyente;
}
