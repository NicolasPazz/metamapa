package ar.utn.ba.ddsi.proxy_metamapa.controller;

import ar.utn.ba.ddsi.proxy_metamapa.client.MetaMapaClient;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.ColeccionDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.PageResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FuenteController {
  private final MetaMapaClient metaMapaClient;

  @GetMapping("/hechos")
  public ResponseEntity<?> getAllHechosVisibles(
      @RequestParam(required = false) String categoria,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "dd-MM-yyyy")
      LocalDate fecha_reporte_desde,
      @RequestParam(required = false)
      @DateTimeFormat(pattern = "dd-MM-yyyy")
      LocalDate fecha_reporte_hasta,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      LocalDateTime fecha_acontecimiento_desde,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      LocalDateTime fecha_acontecimiento_hasta,
      @RequestParam(required = false) String ubicacion,
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer size) {
    
    if (page != null || size != null) {
      int pageNum = page != null ? page : 0;
      int sizeNum = size != null ? size : 100;
      log.info("[FuenteController] - GET - /api/hechos (paginado) - categoria: {}, page: {}, size: {}", categoria, pageNum, sizeNum);
      return metaMapaClient.getAllHechosVisiblesPaginated(
          categoria,
          fecha_reporte_desde, fecha_reporte_hasta,
          fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
          ubicacion, pageNum, sizeNum);
    }
    
    log.info("[FuenteController] - GET - /api/hechos - categoria: {}, fecha_reporte_desde: {}, fecha_reporte_hasta: {}, fecha_acontecimiento_desde: {}, fecha_acontecimiento_hasta: {}, ubicacion: {}",
            categoria, fecha_reporte_desde, fecha_reporte_hasta, fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion);
    return metaMapaClient.getAllHechosVisibles(
        categoria,
        fecha_reporte_desde, fecha_reporte_hasta,
        fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
        ubicacion);
  }

  @GetMapping("/colecciones")
  public ResponseEntity<?> getAllColecciones(
      @RequestParam(required = false) Integer page,
      @RequestParam(required = false) Integer size) {
    
    if (page != null || size != null) {
      int pageNum = page != null ? page : 0;
      int sizeNum = size != null ? size : 100;
      log.info("[FuenteController] - GET - /api/colecciones (paginado) - page: {}, size: {}", pageNum, sizeNum);
      return metaMapaClient.getAllColeccionesPaginated(pageNum, sizeNum);
    }
    
    log.info("[FuenteController] - GET - /api/colecciones");
    return metaMapaClient.getAllColecciones();
  }

  @GetMapping("/colecciones/{handle}/hechos")
  public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      @PathVariable("handle") String handle,
      @RequestParam(required = false) String categoria,

      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_reporte_desde,

      @RequestParam(required = false)
      @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fecha_reporte_hasta,

      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      LocalDateTime fecha_acontecimiento_desde,

      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
      LocalDateTime fecha_acontecimiento_hasta,

      @RequestParam(required = false) String ubicacion
  ) {
    log.info("[FuenteController] - GET - /api/colecciones/{}/hechos - handle: {}, categoria: {}, fecha_reporte_desde: {}, fecha_reporte_hasta: {}, fecha_acontecimiento_desde: {}, fecha_acontecimiento_hasta: {}, ubicacion: {}",
            handle, handle, categoria, fecha_reporte_desde, fecha_reporte_hasta, fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion);
    return metaMapaClient.getHechosColeccionByHandle(
        handle,
        categoria,
        fecha_reporte_desde, fecha_reporte_hasta,
        fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
        ubicacion
    );
  }

}
