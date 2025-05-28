package ar.utn.ba.ddsi.fuentedinamica.service;

import ar.utn.ba.ddsi.fuentedinamica.domain.dto.input.HechoInputDTO;
import ar.utn.ba.ddsi.fuentedinamica.domain.dto.output.HechoOutputDTO;
import java.util.List;

public interface IHechosService {
  public List<HechoOutputDTO> buscarTodos();
  public HechoOutputDTO crear(HechoInputDTO hechoInputDTO);
}
