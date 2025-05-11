package ar.edu.utn.frba.dds.service.impl;

import ar.edu.utn.frba.dds.domain.dto.output.HechoOutputDTO;
import ar.edu.utn.frba.dds.domain.repositorie.IHechosRespository;
import ar.edu.utn.frba.dds.service.IHechosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HechosService implements IHechosService {
  @Autowired
  private IHechosRespository hechosRespository;

  @Override
  public List<HechoOutputDTO> buscarTodos() {
    // TODO
    return List.of();
  }
}
