package ar.utn.ba.ddsi.servicio_agregador.service;

import ar.utn.ba.ddsi.servicio_agregador.domain.dto.output.EtiquetaOutputDTO;
import java.util.List;

public interface IEtiquetasService {
  public List<EtiquetaOutputDTO> buscarTodas();
}