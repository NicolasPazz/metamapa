package ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.dto.external;

import java.util.List;
import lombok.Data;

@Data
public class HechoProxyResponseDTO {
  private Long current_page;
  private List<HechoProxyDTO> data;
  private Long last_page;
}
