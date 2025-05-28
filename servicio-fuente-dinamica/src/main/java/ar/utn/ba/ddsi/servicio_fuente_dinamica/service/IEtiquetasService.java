package ar.utn.ba.ddsi.servicio_fuente_dinamica.service;

import ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.dto.output.EtiquetaOutputDTO;
import java.util.List;

public interface IEtiquetasService {
  public List<EtiquetaOutputDTO> buscarTodas();
}