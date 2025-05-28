package ar.utn.ba.ddsi.servicio_fuente_estatica.service;

import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.dto.input.HechoInputDTO;
import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.dto.output.HechoOutputDTO;
import java.util.List;

public interface IHechosService {
  public List<HechoOutputDTO> buscarTodos();
  public HechoOutputDTO crear(HechoInputDTO hechoInputDTO);
}
