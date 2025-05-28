package ar.utn.ba.ddsi.fuentedinamica.domain.dto.external;

import ar.utn.ba.ddsi.fuentedinamica.domain.dto.external.HechoProxyDTO;
import java.util.List;
import lombok.Data;

@Data
public class HechoProxyResponseDTO {
  private Long current_page;
  private List<HechoProxyDTO> data;
  private Long last_page;
}
