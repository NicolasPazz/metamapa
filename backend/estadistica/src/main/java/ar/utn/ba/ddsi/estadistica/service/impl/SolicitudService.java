package ar.utn.ba.ddsi.estadistica.service.impl;

import ar.utn.ba.ddsi.estadistica.client.AgregadorClient;
import ar.utn.ba.ddsi.estadistica.domain.SolicitudEliminacion;
import ar.utn.ba.ddsi.estadistica.domain.dto.input.SolicitudDTO;
import ar.utn.ba.ddsi.estadistica.service.ISolicitudService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitudService implements ISolicitudService {

  private final AgregadorClient agregadorClient;

  @Override
  public List<SolicitudEliminacion> refrescarSolicitudes() {
    List<SolicitudDTO> dtos;
    try {
      log.info("Obteniendo todas las solicitudes desde el agregador…");
      dtos = agregadorClient.getAllSolicitudes();
    } catch (Exception e) {
      log.error("No se pudieron obtener solicitudes del agregador", e);
      return List.of();
    }
    if (dtos == null || dtos.isEmpty()) {
      log.info("El agregador no devolvió solicitudes.");
      return List.of();
    }

    return dtos.stream()
        .map(this::mapearSolicitud)
        .toList();
  }

  public SolicitudEliminacion mapearSolicitud(SolicitudDTO dto) {
    return new SolicitudEliminacion(
        dto.getSpam()
    );
  }
}
