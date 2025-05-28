package ar.utn.ba.ddsi.servicio_estadisticas.service;

import ar.utn.ba.ddsi.servicio_estadisticas.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.servicio_estadisticas.domain.dto.output.SolicitudOutputDTO;

public interface ISolicitudesService {
  SolicitudOutputDTO crear(SolicitudInputDTO solicitudInputDTO);
}
