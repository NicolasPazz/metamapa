package ar.utn.ba.ddsi.servicio_fuente_dinamica.service;

import ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.dto.output.SolicitudOutputDTO;

public interface ISolicitudesService {
  SolicitudOutputDTO crear(SolicitudInputDTO solicitudInputDTO);
}
