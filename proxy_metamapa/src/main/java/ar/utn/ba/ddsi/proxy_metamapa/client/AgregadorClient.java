package ar.utn.ba.ddsi.proxy_metamapa.client;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.ColeccionDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.SolicitudOutputDTO;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface AgregadorClient {
  Mono<List<HechoDTO>> getAllHechosVisibles(
      String categoria,
      LocalDate fechaReporteDesde,
      LocalDate fechaReporteHasta,
      LocalDate fechaAcontecimientoDesde,
      LocalDate fechaAcontecimientoHasta,
      String ubicacion
  );

  Mono<List<ColeccionDTO>> getAllColecciones();
  Mono<ColeccionDTO> getColeccionByHandle(String handle);
  Mono<SolicitudOutputDTO> crearSolicitudDeEliminacion(SolicitudInputDTO solicitud);
}
