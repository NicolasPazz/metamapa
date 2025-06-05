package ar.utn.ba.ddsi.proxy_metamapa.service.impl;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.ColeccionDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.HechoDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.SolicitudDeEliminacionDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class FuenteService {
  private final WebClient webClient;

  public FuenteService(WebClient.Builder webClientBuilder, @Value("${agregador.url}") String apiUrl) {
    this.webClient = webClientBuilder.baseUrl(apiUrl).build();
  }

  public Mono<List<HechoDTO>> getAllHechosVisibles(
      String categoria,
      String fecha_reporte_desde,
      String fecha_reporte_hasta,
      String fecha_acontecimiento_desde,
      String fecha_acontecimiento_hasta,
      String ubicacion
  ) {
    return webClient
        .get()
        .uri(uriBuilder -> {
          uriBuilder.path("hechos");
          if (categoria != null) uriBuilder.queryParam("categoria", categoria);
          if (fecha_reporte_desde != null) uriBuilder.queryParam("fecha_reporte_desde", fecha_reporte_desde);
          if (fecha_reporte_hasta != null) uriBuilder.queryParam("fecha_reporte_hasta", fecha_reporte_hasta);
          if (fecha_acontecimiento_desde != null) uriBuilder.queryParam("fecha_acontecimiento_desde", fecha_acontecimiento_desde);
          if (fecha_acontecimiento_hasta != null) uriBuilder.queryParam("fecha_acontecimiento_hasta", fecha_acontecimiento_hasta);
          if (ubicacion != null) uriBuilder.queryParam("ubicacion", ubicacion);
          return uriBuilder.build();
        })
        .retrieve()
        .bodyToFlux(HechoDTO.class)
        .collectList();
  }

  public List<HechoDTO> getAllHechosNuevos() {
    return List.of();
  }

  public Mono<List<ColeccionDTO>> getAllColecciones() {
    return webClient
        .get()
        .uri("/colecciones")
        .retrieve()
        .bodyToFlux(ColeccionDTO.class)
        .collectList();
  }

  public Mono<ColeccionDTO> getColeccionById(String id) {
    return webClient
        .get()
        .uri("/colecciones/{id}/hechos", id)
        .retrieve()
        .bodyToMono(ColeccionDTO.class);
  }

  public Mono<SolicitudDeEliminacionDTO> createSolicitudDeEliminacion(SolicitudDeEliminacionDTO solicitud) {
    return webClient
        .post()
        .uri("/solicitudes")
        .bodyValue(solicitud)
        .retrieve()
        .bodyToMono(SolicitudDeEliminacionDTO.class);
  }
}