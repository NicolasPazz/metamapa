package ar.utn.ba.ddsi.proxy_metamapa.service.impl;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.ColeccionDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.SolicitudOutputDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.ResponseEntity;

@Service
public class FuenteService {
  private final WebClient webClient;

  public FuenteService(WebClient.Builder webClientBuilder, @Value("${agregador.url}") String apiUrl) {
    this.webClient = webClientBuilder.baseUrl(apiUrl).build();
  }

  public ResponseEntity<List<HechoDTO>> getAllHechosVisibles(
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
      String ubicacion
  ) {
    List<HechoDTO> hechos = webClient
        .get()
        .uri(uriBuilder -> {
          uriBuilder.path("/hechos");
          if (categoria != null) uriBuilder.queryParam("categoria", categoria);
          if (fecha_reporte_desde != null) uriBuilder.queryParam(
              "fecha_reporte_desde", fecha_reporte_desde);
          if (fecha_reporte_hasta != null) uriBuilder.queryParam(
              "fecha_reporte_hasta", fecha_reporte_hasta);
          if (fecha_acontecimiento_desde != null) uriBuilder.queryParam(
              "fecha_acontecimiento_desde", fecha_acontecimiento_desde);
          if (fecha_acontecimiento_hasta != null) uriBuilder.queryParam(
              "fecha_acontecimiento_hasta", fecha_acontecimiento_hasta);
          if (ubicacion != null) uriBuilder.queryParam("ubicacion", ubicacion);
          return uriBuilder.build();
        })
        .retrieve()
        .bodyToFlux(HechoDTO.class)
        .collectList()
        .block();

    return ResponseEntity.of(Optional.ofNullable(hechos));
  }

  public ResponseEntity<List<ColeccionDTO>> getAllColecciones() {
    List<ColeccionDTO> colecciones = webClient
        .get()
        .uri("/colecciones")
        .retrieve()
        .bodyToFlux(ColeccionDTO.class)
        .collectList()
        .block();

    return ResponseEntity.of(Optional.ofNullable(colecciones));
  }

  public ResponseEntity<List<HechoDTO>> getColeccionByHandle(String handle) {
    List<HechoDTO> hechos = webClient
        .get()
        .uri("/colecciones/{handle}/hechos", handle)
        .retrieve()
        .bodyToFlux(HechoDTO.class)
        .collectList()
        .block();
    return ResponseEntity.of(Optional.ofNullable(hechos));
  }

  public ResponseEntity<SolicitudOutputDTO> crearSolicitudDeEliminacion(SolicitudInputDTO solicitud) {
    SolicitudOutputDTO resultado = webClient
        .post()
        .uri("/solicitudes")
        .bodyValue(solicitud)
        .retrieve()
        .bodyToMono(SolicitudOutputDTO.class)
        .block();

    return ResponseEntity.of(Optional.ofNullable(resultado));
  }
}