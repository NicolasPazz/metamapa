package ar.edu.utn.frba.dds.domain.dto.input;

import ar.edu.utn.frba.dds.domain.request.validacion.Validacion;
import lombok.Data;
import java.util.Set;

@Data
public class SolicitudInputDTO {
  private Long idHecho;
  private String textoFundamentos;
  private Long idContribuyente;
  private Set<Validacion> validaciones;
}
