package ar.utn.ba.ddsi.monomodule.service;

import ar.utn.ba.ddsi.monomodule.domain.dto.input.HechoInputDTO;
import ar.utn.ba.ddsi.monomodule.domain.dto.output.HechoOutputDTO;
import java.util.List;

public interface IHechosService {
  public List<HechoOutputDTO> buscarTodos();
  public HechoOutputDTO crear(HechoInputDTO hechoInputDTO);
}
