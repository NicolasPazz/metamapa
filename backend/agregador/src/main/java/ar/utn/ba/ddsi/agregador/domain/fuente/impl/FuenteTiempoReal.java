package ar.utn.ba.ddsi.agregador.domain.fuente.impl;

import ar.utn.ba.ddsi.agregador.dto.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.fuente.AFuente;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@NoArgsConstructor
@Entity
@DiscriminatorValue("TIEMPO_REAL")
public class FuenteTiempoReal extends AFuente {
  @Transient
  @Getter
  @Setter
  private WebClient webClient;

  public FuenteTiempoReal(Long id, String nombre, WebClient webClient) {
    super(id, nombre);
    this.webClient = webClient;
  }

  @Transient
  @Override
  public ResponseEntity<List<HechoDTO>> getAllHechosVisibles(
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
      String ubicacion) {
    // Construir query string manualmente para evitar problemas con UriBuilder y hosts con guiones bajos
    StringBuilder queryBuilder = new StringBuilder("/api/hechos");
    boolean firstParam = true;
    if (categoria != null) {
      queryBuilder.append(firstParam ? "?" : "&").append("categoria=").append(java.net.URLEncoder.encode(categoria, java.nio.charset.StandardCharsets.UTF_8));
      firstParam = false;
    }
    if (fecha_reporte_desde != null) {
      queryBuilder.append(firstParam ? "?" : "&").append("fecha_reporte_desde=").append(fecha_reporte_desde);
      firstParam = false;
    }
    if (fecha_reporte_hasta != null) {
      queryBuilder.append(firstParam ? "?" : "&").append("fecha_reporte_hasta=").append(fecha_reporte_hasta);
      firstParam = false;
    }
    if (fecha_acontecimiento_desde != null) {
      queryBuilder.append(firstParam ? "?" : "&").append("fecha_acontecimiento_desde=").append(fecha_acontecimiento_desde);
      firstParam = false;
    }
    if (fecha_acontecimiento_hasta != null) {
      queryBuilder.append(firstParam ? "?" : "&").append("fecha_acontecimiento_hasta=").append(fecha_acontecimiento_hasta);
      firstParam = false;
    }
    if (ubicacion != null) {
      queryBuilder.append(firstParam ? "?" : "&").append("ubicacion=").append(java.net.URLEncoder.encode(ubicacion, java.nio.charset.StandardCharsets.UTF_8));
    }
    
    List<HechoDTO> hechos = webClient.get()
        .uri(queryBuilder.toString())
        .retrieve()
        .bodyToFlux(HechoDTO.class)
        .collectList()
        .block();
    return ResponseEntity.of(Optional.ofNullable(hechos));
  }

  @Transient
  @Override
  public ResponseEntity<List<HechoDTO>> getAllHechosNuevos() {
    return ResponseEntity.ok(List.of());
  }

  @Transient
  @Override
  public ResponseEntity<List<ColeccionDTO>> getAllColecciones() {
    try {
      log.info("Obteniendo colecciones de fuente tiempo real: {}", getNombre());
      
      if (webClient == null) {
        log.error("WebClient no está inicializado para fuente: {}", getNombre());
        return ResponseEntity.ok(List.of());
      }
      
      List<ColeccionDTO> colecciones = webClient.get()
          .uri("/api/colecciones")
          .retrieve()
          .bodyToFlux(ColeccionDTO.class)
          .collectList()
          .block();
      log.info("Colecciones obtenidas: {}", colecciones != null ? colecciones.size() : 0);
      return ResponseEntity.of(Optional.ofNullable(colecciones));
    } catch (Exception e) {
      log.error("Error obteniendo colecciones de fuente {}: {}", getNombre(), e.getMessage(), e);
      return ResponseEntity.ok(List.of());
    }
  }

  @Transient
  @Override
  public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      UUID handle,
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
      String ubicacion,
      String modo) {
    // Construir query string manualmente para evitar problemas con UriBuilder y hosts con guiones bajos
    StringBuilder queryBuilder = new StringBuilder(String.format("/colecciones/%s/hechos", handle));
    boolean firstParam = true;
    if (categoria != null) {
      queryBuilder.append(firstParam ? "?" : "&").append("categoria=").append(java.net.URLEncoder.encode(categoria, java.nio.charset.StandardCharsets.UTF_8));
      firstParam = false;
    }
    if (fecha_reporte_desde != null) {
      queryBuilder.append(firstParam ? "?" : "&").append("fecha_reporte_desde=").append(fecha_reporte_desde);
      firstParam = false;
    }
    if (fecha_reporte_hasta != null) {
      queryBuilder.append(firstParam ? "?" : "&").append("fecha_reporte_hasta=").append(fecha_reporte_hasta);
      firstParam = false;
    }
    if (fecha_acontecimiento_desde != null) {
      queryBuilder.append(firstParam ? "?" : "&").append("fecha_acontecimiento_desde=").append(fecha_acontecimiento_desde);
      firstParam = false;
    }
    if (fecha_acontecimiento_hasta != null) {
      queryBuilder.append(firstParam ? "?" : "&").append("fecha_acontecimiento_hasta=").append(fecha_acontecimiento_hasta);
      firstParam = false;
    }
    if (ubicacion != null) {
      queryBuilder.append(firstParam ? "?" : "&").append("ubicacion=").append(java.net.URLEncoder.encode(ubicacion, java.nio.charset.StandardCharsets.UTF_8));
      firstParam = false;
    }
    if (modo != null) {
      queryBuilder.append(firstParam ? "?" : "&").append("modo=").append(java.net.URLEncoder.encode(modo, java.nio.charset.StandardCharsets.UTF_8));
    }
    
    List<HechoDTO> hechos = webClient.get()
        .uri(queryBuilder.toString())
        .retrieve()
        .bodyToFlux(HechoDTO.class)
        .collectList()
        .block();

    return ResponseEntity.ok(hechos);
  }
}
