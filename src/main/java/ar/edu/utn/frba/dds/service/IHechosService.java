package ar.edu.utn.frba.dds.service;

import ar.edu.utn.frba.dds.domain.dto.output.HechoOutputDTO;
import java.util.List;

public interface IHechosService {
  List<HechoOutputDTO> buscarTodos();
}
