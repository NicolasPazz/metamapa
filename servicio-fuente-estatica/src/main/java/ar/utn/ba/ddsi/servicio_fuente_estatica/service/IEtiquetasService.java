package ar.utn.ba.ddsi.servicio_fuente_estatica.service;

import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.dto.output.EtiquetaOutputDTO;
import java.util.List;

public interface IEtiquetasService {
  public List<EtiquetaOutputDTO> buscarTodas();
}