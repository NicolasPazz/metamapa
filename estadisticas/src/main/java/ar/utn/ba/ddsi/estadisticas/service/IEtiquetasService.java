package ar.utn.ba.ddsi.estadisticas.service;

import ar.utn.ba.ddsi.estadisticas.domain.dto.output.EtiquetaOutputDTO;
import java.util.List;

public interface IEtiquetasService {
  public List<EtiquetaOutputDTO> buscarTodas();
}