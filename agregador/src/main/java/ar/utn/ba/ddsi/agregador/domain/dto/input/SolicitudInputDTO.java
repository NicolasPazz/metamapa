package ar.utn.ba.ddsi.agregador.domain.dto.input;

import ar.utn.ba.ddsi.agregador.domain.request.validacion.Validacion;
import java.util.Set;
import lombok.Data;

@Data
public class SolicitudInputDTO {
  private Long idHecho;
  private String textoFundamentos;
  private Long idContribuyente;
  private Set<Validacion> validaciones;
}
