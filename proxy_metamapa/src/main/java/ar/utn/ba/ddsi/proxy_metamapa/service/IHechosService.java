package ar.utn.ba.ddsi.proxy_metamapa.service;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.HechoOutputDTO;
import java.util.List;

public interface IHechosService {
  public List<HechoOutputDTO> buscarTodos();
  public HechoOutputDTO crear(HechoInputDTO hechoInputDTO);
}
