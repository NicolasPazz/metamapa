package ar.utn.ba.ddsi.fuenteproxy.service;

import ar.utn.ba.ddsi.fuenteproxy.domain.dto.input.HechoInputDTO;
import ar.utn.ba.ddsi.fuenteproxy.domain.dto.output.HechoOutputDTO;
import java.util.List;

public interface IHechosService {
  public List<HechoOutputDTO> buscarTodos();
  public HechoOutputDTO crear(HechoInputDTO hechoInputDTO);
}
