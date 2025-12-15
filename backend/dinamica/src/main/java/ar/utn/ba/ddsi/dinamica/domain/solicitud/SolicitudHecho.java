package ar.utn.ba.ddsi.dinamica.domain.solicitud;

import ar.utn.ba.ddsi.dinamica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.TransicionEstadoOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.hecho.ContenidoMultimedia;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "solicitud_hecho")
@Builder
public class SolicitudHecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "contribuyente_id", nullable = true)
  private Long idContribuyente; // Null para contribuyentes anónimos

  @NotBlank(message = "El título es obligatorio")
  @Column(name = "titulo", nullable = false)
  private String titulo;

  @NotBlank(message = "La descripción es obligatoria")
  @Column(name = "descripcion", nullable = false, length = 2000)
  private String descripcion;

  @NotBlank(message = "La categoría es obligatoria")
  @Column(name = "categoria", nullable = false)
  private String categoria;

  @NotNull(message = "La latitud es obligatoria")
  @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
  @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
  @Column(name = "latitud", nullable = false)
  private Double latitud;

  @NotNull(message = "La longitud es obligatoria")
  @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
  @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
  @Column(name = "longitud", nullable = false)
  private Double longitud;

  @NotNull(message = "La fecha de acontecimiento es obligatoria")
  @Column(name = "fechaDeAcontecimiento", nullable = false)
  private LocalDate fechaDeAcontecimiento;

  @OneToMany(mappedBy = "solicitudHecho", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ContenidoMultimedia> multimedia = new ArrayList<>();

  @NotNull(message = "La fecha de carga es obligatoria")
  @Column(name = "fechaDeCarga", nullable = false)
  @Builder.Default
  private LocalDateTime fechaDeCargaSolicitud = LocalDateTime.now();

  @Enumerated(EnumType.STRING)
  @Column(name="estado", nullable = false)
  private EstadoSolicitud estado;

  @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "solicitud_id", referencedColumnName = "id")
  @Builder.Default
  private List<TransicionEstadoSolicitud> transiciones = new ArrayList<>();

  public void cambiarEstado(EstadoSolicitud nuevoEstado) {
    this.transiciones.add(new TransicionEstadoSolicitud(nuevoEstado, LocalDateTime.now()));
    this.estado = nuevoEstado;
  }

  public boolean esAnonimo() {
    return this.idContribuyente == null;
  }

  public void validarModificacionPorUsuario(Long idContribuyenteQueModifica) {
    if (this.estado != EstadoSolicitud.PENDIENTE) {
      throw new IllegalStateException("Solo se pueden modificar solicitudes en estado PENDIENTE");
    }

    if (this.esAnonimo()) {
      throw new IllegalStateException("Las solicitudes anónimas no pueden ser modificadas");
    }

    if (!this.idContribuyente.equals(idContribuyenteQueModifica)) {
      throw new IllegalArgumentException("No está autorizado para modificar esta solicitud");
    }

    if (this.fechaDeCargaSolicitud.plusDays(7).isBefore(LocalDateTime.now())) {
      throw new IllegalStateException("El período de modificación de 7 días ha expirado");
    }
  }

  public SolicitudOutputDTO toOutputDTO() {
    var dto = new SolicitudOutputDTO();
    dto.setId(this.getId());
    dto.setIdContribuyente(this.getIdContribuyente());
    dto.setTitulo(this.getTitulo());
    dto.setDescripcion(this.getDescripcion());
    dto.setNombreCategoria(this.getCategoria());
    dto.setLatitud(this.getLatitud());
    dto.setLongitud(this.getLongitud());
    dto.setFechaDeAcontecimiento(this.getFechaDeAcontecimiento());
    if (this.getMultimedia() != null && !this.getMultimedia().isEmpty()) {
      dto.setContenidoMultimedia(this.getMultimedia().stream()
          .map(ContenidoMultimedia::toOutputDTO)
          .toList());
    }
    dto.setFechaDeCargaSolicitud(this.getFechaDeCargaSolicitud());
    dto.setEstado(this.getEstado());

    if (this.getTransiciones() != null && !this.getTransiciones().isEmpty()) {
      dto.setTransiciones(this.getTransiciones().stream()
          .map(t -> new TransicionEstadoOutputDTO(
              t.getEstado(),
              t.getFecha()
          ))
          .toList());
    }

    return dto;
  }
}
