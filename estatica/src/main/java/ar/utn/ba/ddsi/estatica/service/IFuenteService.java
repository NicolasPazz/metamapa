package ar.utn.ba.ddsi.estatica.service;

import ar.utn.ba.ddsi.estatica.domain.dto.output.HechoOutputDTO;
import java.util.List;

public interface IFuenteService {
  List<HechoOutputDTO> getAllHechosNuevos();
}
