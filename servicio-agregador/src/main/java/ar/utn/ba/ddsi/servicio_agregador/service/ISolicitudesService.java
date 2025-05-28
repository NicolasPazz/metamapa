package ar.utn.ba.ddsi.servicio_agregador.service;

import ar.utn.ba.ddsi.servicio_agregador.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.servicio_agregador.domain.dto.output.SolicitudOutputDTO;

public interface ISolicitudesService {
  SolicitudOutputDTO crear(SolicitudInputDTO solicitudInputDTO);
}
