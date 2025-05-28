package ar.utn.ba.ddsi.servicio_fuente_estatica.domain.dto.external;

import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.dto.external.HechoProxyDTO;
import java.util.List;
import lombok.Data;

@Data
public class HechoProxyResponseDTO {
  private Long current_page;
  private List<HechoProxyDTO> data;
  private Long last_page;
}
