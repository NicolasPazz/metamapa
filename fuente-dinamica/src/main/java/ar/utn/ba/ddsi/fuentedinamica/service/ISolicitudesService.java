package ar.utn.ba.ddsi.fuentedinamica.service;

import ar.utn.ba.ddsi.fuentedinamica.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.fuentedinamica.domain.dto.output.SolicitudOutputDTO;

public interface ISolicitudesService {
  SolicitudOutputDTO crear(SolicitudInputDTO solicitudInputDTO);
}
