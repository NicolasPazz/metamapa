package ar.edu.utn.frba.dds.service;

import ar.edu.utn.frba.dds.domain.dto.input.SolicitudInputDTO;
import ar.edu.utn.frba.dds.domain.dto.output.SolicitudOutputDTO;

public interface ISolicitudesService {
  SolicitudOutputDTO crear(SolicitudInputDTO solicitudInputDTO);
}
