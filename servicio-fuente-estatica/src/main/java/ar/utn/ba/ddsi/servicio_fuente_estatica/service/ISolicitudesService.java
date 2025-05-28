package ar.utn.ba.ddsi.servicio_fuente_estatica.service;

import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.dto.output.SolicitudOutputDTO;

public interface ISolicitudesService {
  SolicitudOutputDTO crear(SolicitudInputDTO solicitudInputDTO);
}
