package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.output.SolicitudOutputDTO;
import org.springframework.http.ResponseEntity;

public interface ISolicitudesService {
  public boolean esValida(String textoFundamentos);
  public ResponseEntity<?> crear(SolicitudInputDTO solicitudInputDTO);
  public ResponseEntity<?> actualizarEstado(Long id, SolicitudOutputDTO dto);
  public void aceptar(Long idSolicitud);
  public void rechazar(Long idSolicitud);
}

