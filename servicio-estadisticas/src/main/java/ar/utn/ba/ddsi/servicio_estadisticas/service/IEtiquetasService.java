package ar.utn.ba.ddsi.servicio_estadisticas.service;

import ar.utn.ba.ddsi.servicio_estadisticas.domain.dto.output.EtiquetaOutputDTO;
import java.util.List;

public interface IEtiquetasService {
  public List<EtiquetaOutputDTO> buscarTodas();
}