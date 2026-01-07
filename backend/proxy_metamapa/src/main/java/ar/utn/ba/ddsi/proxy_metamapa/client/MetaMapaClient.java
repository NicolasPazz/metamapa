package ar.utn.ba.ddsi.proxy_metamapa.client;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.ColeccionDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.PageResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface MetaMapaClient {
  ResponseEntity<List<HechoDTO>> getAllHechosVisibles(
      String categoria,
      LocalDate fechaReporteDesde,
      LocalDate fechaReporteHasta,
      LocalDateTime fechaAcontecimientoDesde,
      LocalDateTime fechaAcontecimientoHasta,
      String ubicacion
  );

  ResponseEntity<List<ColeccionDTO>> getAllColecciones();

  ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      String handle,
      String categoria,
      LocalDate fecha_reporte_desde,
      LocalDate fecha_reporte_hasta,
      LocalDateTime fecha_acontecimiento_desde,
      LocalDateTime fecha_acontecimiento_hasta,
      String ubicacion
  );

  ResponseEntity<PageResponse<HechoDTO>> getAllHechosVisiblesPaginated(
      String categoria,
      LocalDate fechaReporteDesde,
      LocalDate fechaReporteHasta,
      LocalDateTime fechaAcontecimientoDesde,
      LocalDateTime fechaAcontecimientoHasta,
      String ubicacion,
      int page,
      int size
  );

  ResponseEntity<PageResponse<ColeccionDTO>> getAllColeccionesPaginated(
      int page,
      int size
  );
}
