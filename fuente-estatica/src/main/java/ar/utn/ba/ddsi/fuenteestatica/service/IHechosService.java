package ar.utn.ba.ddsi.fuenteestatica.service;

import ar.utn.ba.ddsi.fuenteestatica.domain.dto.input.HechoInputDTO;
import ar.utn.ba.ddsi.fuenteestatica.domain.dto.output.HechoOutputDTO;
import java.util.List;

public interface IHechosService {
  public List<HechoOutputDTO> buscarTodos();
  public HechoOutputDTO crear(HechoInputDTO hechoInputDTO);
}
