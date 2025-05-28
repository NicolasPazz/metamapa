package ar.utn.ba.ddsi.monomodule.service;

import ar.utn.ba.ddsi.monomodule.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.monomodule.domain.dto.output.SolicitudOutputDTO;

public interface ISolicitudesService {
  SolicitudOutputDTO crear(SolicitudInputDTO solicitudInputDTO);
}
