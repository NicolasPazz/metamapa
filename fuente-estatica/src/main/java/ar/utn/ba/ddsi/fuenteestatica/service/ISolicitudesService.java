package ar.utn.ba.ddsi.fuenteestatica.service;

import ar.utn.ba.ddsi.fuenteestatica.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.fuenteestatica.domain.dto.output.SolicitudOutputDTO;

public interface ISolicitudesService {
  SolicitudOutputDTO crear(SolicitudInputDTO solicitudInputDTO);
}
