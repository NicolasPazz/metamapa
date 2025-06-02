package ar.utn.ba.ddsi.proxy_metamapa.service;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.EtiquetaOutputDTO;
import java.util.List;

public interface IEtiquetasService {
  public List<EtiquetaOutputDTO> buscarTodas();
}