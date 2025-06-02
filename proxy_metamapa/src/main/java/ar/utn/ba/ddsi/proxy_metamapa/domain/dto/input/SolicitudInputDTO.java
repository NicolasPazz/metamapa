package ar.utn.ba.ddsi.proxy_metamapa.domain.dto.input;

import ar.utn.ba.ddsi.proxy_metamapa.domain.request.validacion.Validacion;
import lombok.Data;
import java.util.Set;

@Data
public class SolicitudInputDTO {
  private Long idHecho;
  private String textoFundamentos;
  private Long idContribuyente;
  private Set<Validacion> validaciones;
}
