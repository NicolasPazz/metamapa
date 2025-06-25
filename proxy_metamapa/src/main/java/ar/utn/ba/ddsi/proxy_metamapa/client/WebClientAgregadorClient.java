package ar.utn.ba.ddsi.proxy_metamapa.client;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.ColeccionDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.proxy_metamapa.service.impl.FuenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.util.List;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class WebClientAgregadorClient implements AgregadorClient {

  private final FuenteService fuenteService;

  @Override
  public ResponseEntity<List<HechoDTO>> getAllHechosVisibles(String categoria, LocalDate fechaReporteDesde, LocalDate fechaReporteHasta, LocalDate fechaAcontecimientoDesde, LocalDate fechaAcontecimientoHasta, String ubicacion) {
    return fuenteService.getAllHechosVisibles(
        categoria,
        fechaReporteDesde,
        fechaReporteHasta,
        fechaAcontecimientoDesde,
        fechaAcontecimientoHasta,
        ubicacion
    );
  }

  @Override
  public ResponseEntity<List<ColeccionDTO>> getAllColecciones() {
    return fuenteService.getAllColecciones();
  }

  @Override
  public ResponseEntity<List<HechoDTO>> getColeccionByHandle(String handle) {
    return fuenteService.getColeccionByHandle(handle);
  }

  @Override
  public ResponseEntity<SolicitudOutputDTO> crearSolicitudDeEliminacion(SolicitudInputDTO solicitud) {
    return fuenteService.crearSolicitudDeEliminacion(solicitud);
  }
}
