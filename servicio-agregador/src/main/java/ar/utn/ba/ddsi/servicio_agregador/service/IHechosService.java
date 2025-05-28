package ar.utn.ba.ddsi.servicio_agregador.service;

import ar.utn.ba.ddsi.servicio_agregador.domain.dto.input.HechoInputDTO;
import ar.utn.ba.ddsi.servicio_agregador.domain.dto.output.HechoOutputDTO;
import java.util.List;

public interface IHechosService {
  public List<HechoOutputDTO> buscarTodos();
  public HechoOutputDTO crear(HechoInputDTO hechoInputDTO);
}
