package ar.utn.ba.ddsi.fuentedinamica.service;

import ar.utn.ba.ddsi.fuentedinamica.domain.dto.output.EtiquetaOutputDTO;
import java.util.List;

public interface IEtiquetasService {
  public List<EtiquetaOutputDTO> buscarTodas();
}