package ar.utn.ba.ddsi.proxy_metamapa.client.impl;

import ar.utn.ba.ddsi.proxy_metamapa.client.MetaMapaClient;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.ColeccionDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.PageResponse;
import ar.utn.ba.ddsi.proxy_metamapa.service.FuenteService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Profile("prod-metamapa-external")
@RequiredArgsConstructor
public class WebClientMetaMapaClient implements MetaMapaClient {

  private final FuenteService fuenteService;

  @Override
  public ResponseEntity<List<HechoDTO>> getAllHechosVisibles(String categoria, LocalDate fechaReporteDesde, LocalDate fechaReporteHasta, LocalDateTime fechaAcontecimientoDesde, LocalDateTime fechaAcontecimientoHasta, String ubicacion) {
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
  public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      String handle,
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
      String ubicacion
  ) {
    return fuenteService.getHechosColeccionByHandle(
        handle,
        categoria,
        fecha_reporte_desde, fecha_reporte_hasta,
        fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
        ubicacion
    );
  }

  @Override
  public ResponseEntity<PageResponse<HechoDTO>> getAllHechosVisiblesPaginated(
      String categoria,
      LocalDate fechaReporteDesde,
      LocalDate fechaReporteHasta,
      LocalDateTime fechaAcontecimientoDesde,
      LocalDateTime fechaAcontecimientoHasta,
      String ubicacion,
      int page,
      int size
  ) {
    return fuenteService.getAllHechosVisiblesPaginated(
        categoria,
        fechaReporteDesde,
        fechaReporteHasta,
        fechaAcontecimientoDesde,
        fechaAcontecimientoHasta,
        ubicacion,
        page,
        size
    );
  }

  @Override
  public ResponseEntity<PageResponse<ColeccionDTO>> getAllColeccionesPaginated(
      int page,
      int size
  ) {
    return fuenteService.getAllColeccionesPaginated(page, size);
  }
}
