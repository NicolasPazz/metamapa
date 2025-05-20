package ar.utn.frba.dds.metamapa.service;

import ar.utn.frba.dds.metamapa.domain.dto.output.EtiquetaOutputDTO;
import java.util.List;

public interface IEtiquetasService {
  public List<EtiquetaOutputDTO> buscarTodas();
}