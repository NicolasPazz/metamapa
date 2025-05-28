package ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.dto.output;

import ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.request.EstadoSolicitud;
import lombok.Data;
import java.time.LocalDate;

@Data
public class SolicitudOutputDTO {
  private Long id;
  private Long idHecho;
  private String textoFundamentos;
  private LocalDate fecha;
  private EstadoSolicitud estado;
  private Long idContribuyente;
}
