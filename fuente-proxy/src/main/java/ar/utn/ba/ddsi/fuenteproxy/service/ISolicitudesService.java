package ar.utn.ba.ddsi.fuenteproxy.service;

import ar.utn.ba.ddsi.fuenteproxy.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.fuenteproxy.domain.dto.output.SolicitudOutputDTO;

public interface ISolicitudesService {
  SolicitudOutputDTO crear(SolicitudInputDTO solicitudInputDTO);
}
