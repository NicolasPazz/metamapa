package ar.utn.ba.ddsi.fuenteestatica.service;

import ar.utn.ba.ddsi.fuenteestatica.domain.dto.output.EtiquetaOutputDTO;
import java.util.List;

public interface IEtiquetasService {
  public List<EtiquetaOutputDTO> buscarTodas();
}