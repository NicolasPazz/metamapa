package ar.edu.utn.frba.dds.domain.dto.output;

import ar.edu.utn.frba.dds.domain.request.EstadoSolicitud;
import ar.edu.utn.frba.dds.domain.request.TransicionEstadoSolicitud;
import ar.edu.utn.frba.dds.domain.request.validacion.Validacion;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class SolicitudOutputDTO {
  private Long id;
  private Long idHecho;
  private String textoFundamentos;
  private LocalDate fecha;
  private EstadoSolicitud estado;
  private Long idContribuyente;
}
