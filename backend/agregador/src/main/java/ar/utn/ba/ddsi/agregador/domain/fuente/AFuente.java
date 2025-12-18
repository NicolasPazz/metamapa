package ar.utn.ba.ddsi.agregador.domain.fuente;

import ar.utn.ba.ddsi.agregador.dto.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "fuente")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING)
public abstract class AFuente {

  @Id
  private Long id;

  @Column(name = "nombre", nullable = false)
  private String nombre;

  public AFuente(Long id, String nombre) {
    this.id = id;
    this.nombre = nombre;
  }

  @Transient
  public abstract ResponseEntity<List<HechoDTO>> getAllHechosVisibles(
      String categoria,
      LocalDate fecha_reporte_desde,
      LocalDate fecha_reporte_hasta,
      LocalDateTime fecha_acontecimiento_desde,
      LocalDateTime fecha_acontecimiento_hasta,
      String ubicacion);

  @Transient
  public abstract ResponseEntity<List<HechoDTO>> getAllHechosNuevos();

  @Transient
  public abstract ResponseEntity<List<ColeccionDTO>> getAllColecciones();

  @Transient
  public abstract ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      UUID handle,
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
      String ubicacion,
      String modo);
}