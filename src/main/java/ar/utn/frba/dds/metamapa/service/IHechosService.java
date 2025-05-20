package ar.utn.frba.dds.metamapa.service;

import ar.utn.frba.dds.metamapa.domain.dto.input.HechoInputDTO;
import ar.utn.frba.dds.metamapa.domain.dto.output.HechoOutputDTO;
import java.util.List;

public interface IHechosService {
  public List<HechoOutputDTO> buscarTodos();
  public HechoOutputDTO crear(HechoInputDTO hechoInputDTO);
}
