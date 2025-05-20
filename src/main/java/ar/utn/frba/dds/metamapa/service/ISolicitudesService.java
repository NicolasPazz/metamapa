package ar.utn.frba.dds.metamapa.service;

import ar.utn.frba.dds.metamapa.domain.dto.input.SolicitudInputDTO;
import ar.utn.frba.dds.metamapa.domain.dto.output.SolicitudOutputDTO;

public interface ISolicitudesService {
  SolicitudOutputDTO crear(SolicitudInputDTO solicitudInputDTO);
}
