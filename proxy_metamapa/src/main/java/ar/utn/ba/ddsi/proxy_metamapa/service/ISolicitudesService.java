package ar.utn.ba.ddsi.proxy_metamapa.service;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.SolicitudOutputDTO;

public interface ISolicitudesService {
  SolicitudOutputDTO crear(SolicitudInputDTO solicitudInputDTO);
}
