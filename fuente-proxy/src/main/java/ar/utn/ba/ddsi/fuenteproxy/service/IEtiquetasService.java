package ar.utn.ba.ddsi.fuenteproxy.service;

import ar.utn.ba.ddsi.fuenteproxy.domain.dto.output.EtiquetaOutputDTO;
import java.util.List;

public interface IEtiquetasService {
  public List<EtiquetaOutputDTO> buscarTodas();
}