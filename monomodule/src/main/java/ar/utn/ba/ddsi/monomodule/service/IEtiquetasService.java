package ar.utn.ba.ddsi.monomodule.service;

import ar.utn.ba.ddsi.monomodule.domain.dto.output.EtiquetaOutputDTO;
import java.util.List;

public interface IEtiquetasService {
  public List<EtiquetaOutputDTO> buscarTodas();
}