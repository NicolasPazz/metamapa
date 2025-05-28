package ar.utn.ba.ddsi.servicio_fuente_proxy.service;

import ar.utn.ba.ddsi.servicio_fuente_proxy.domain.dto.input.HechoInputDTO;
import ar.utn.ba.ddsi.servicio_fuente_proxy.domain.dto.output.HechoOutputDTO;
import java.util.List;

public interface IHechosService {
  public List<HechoOutputDTO> buscarTodos();
  public HechoOutputDTO crear(HechoInputDTO hechoInputDTO);
}
