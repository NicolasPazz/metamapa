package ar.utn.ba.ddsi.proxy_metamapa.client;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.ColeccionDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.HechoDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.SolicitudDeEliminacionDTO;
import ar.utn.ba.ddsi.proxy_metamapa.service.impl.FuenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.util.List;

@Service
@Profile("prod")
@RequiredArgsConstructor
public class WebClientAgregadorClient implements AgregadorClient {

  private final FuenteService fuenteService;

  @Override
  public Mono<List<HechoDTO>> getAllHechosVisibles(String categoria, LocalDate fechaReporteDesde, LocalDate fechaReporteHasta, LocalDate fechaAcontecimientoDesde, LocalDate fechaAcontecimientoHasta, String ubicacion) {
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
  public Mono<List<ColeccionDTO>> getAllColecciones() {
    return fuenteService.getAllColecciones();
  }

  @Override
  public Mono<ColeccionDTO> getColeccionByHandle(String handle) {
    return fuenteService.getColeccionByHandle(handle);
  }

  @Override
  public Mono<SolicitudDeEliminacionDTO> crearSolicitudDeEliminacion(SolicitudDeEliminacionDTO solicitud) {
    return fuenteService.crearSolicitudDeEliminacion(solicitud);
  }
}
