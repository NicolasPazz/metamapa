package ar.utn.ba.ddsi.agregador.domain.fuente.impl;

import ar.utn.ba.ddsi.agregador.dto.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.fuente.AFuente;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("CRON")
public class FuenteCron extends AFuente {

  @Transient
  private WebClient webClient;

  public FuenteCron(Long id, String nombre, WebClient webClient) {
    super(id, nombre);
    this.webClient = webClient;
  }


  @Override
  public ResponseEntity<List<HechoDTO>> getAllHechosVisibles(
          String categoria,
          LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
          LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
          String ubicacion) {
    return ResponseEntity.ok(List.of());
  }

  @Override
  public ResponseEntity<List<HechoDTO>> getAllHechosNuevos() {
    List<HechoDTO> hechos = webClient.get()
        .uri("/hechos/nuevos")
        .retrieve()
        .bodyToFlux(HechoDTO.class)
        .collectList()
        .block();
    return ResponseEntity.ok(hechos);
  }

  @Override
  public ResponseEntity<List<ColeccionDTO>> getAllColecciones() {
    return ResponseEntity.ok(List.of());
  }

  @Override
  public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      UUID handle,
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
      String ubicacion,
      String modo) {
    return ResponseEntity.of(Optional.empty());
  }
}