package ar.utn.ba.ddsi.estadisticas.service;

import ar.utn.ba.ddsi.estadisticas.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.estadisticas.domain.dto.output.SolicitudOutputDTO;

public interface ISolicitudesService {
  SolicitudOutputDTO crear(SolicitudInputDTO solicitudInputDTO);
}
