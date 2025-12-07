package ar.utn.ba.ddsi.agregador.domain.hecho;

import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.ContenidoMultimediaDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.TransicionEstadoHechoDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.multimedia.ContenidoMultimedia;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import java.util.stream.Collectors;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "hecho")
public class Hecho {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Long id;

  @Column(name = "titulo")
  private String titulo;

  @Column(name = "descripcion", columnDefinition = "TEXT")
  private String descripcion;

  @Column(name = "latitud")
  private Double latitud;

  @Column(name = "longitud")
  private Double longitud;

  @Column(name = "fecha_de_acontecimiento")
  private LocalDate fechaDeAcontecimiento;

  @Column(name = "fecha_de_carga")
  private LocalDateTime fechaDeCarga = LocalDateTime.now();

  @Enumerated(EnumType.ORDINAL)
  private OrigenHecho origen;

  @Enumerated(EnumType.ORDINAL)
  private EstadoHecho estado;

  @Column(name="contribuyente_id", nullable = true)
  private Long contribuyente;

  @OneToMany
  @JoinColumn(name = "etiqueta_id")
  @Builder.Default
  private Set<Etiqueta> etiquetas = new HashSet<>();

  @OneToMany
  @JoinColumn(name = "multimedia_id")
  @Builder.Default
  private List<ContenidoMultimedia> multimedia = new ArrayList<>();

  @ElementCollection
  @Builder.Default
  private List<TransicionEstadoHecho> transiciones = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "categoria_id")
  private Categoria categoria;

  public void transicionarEstado(EstadoHecho estado) {
    this.transiciones.add(new TransicionEstadoHecho(estado));
    this.estado = estado;
  }

  public HechoDTO mapToDTO() {
    HechoDTO dto = new HechoDTO();
    dto.setId(this.getId());
    dto.setTitulo(this.getTitulo());
    dto.setDescripcion(this.getDescripcion());
    dto.setCategoria(this.getCategoria().getNombre());
    dto.setLatitud(this.getLatitud());
    dto.setLongitud(this.getLongitud());
    dto.setFechaDeAcontecimiento(this.getFechaDeAcontecimiento());
    dto.setFechaDeCarga(this.getFechaDeCarga());
    dto.setOrigen(this.getOrigen().name());
    dto.setEstado(this.getEstado().name());

    /*
    if (this.getContribuyente() != null) {
      ContribuyenteDTO contribuyenteDTO = new ContribuyenteDTO();
      contribuyenteDTO.setNombre(this.getContribuyente().getNombre());
      contribuyenteDTO.setApellido(this.getContribuyente().getApellido());
      contribuyenteDTO.setFechaDeNacimiento(
          Optional.ofNullable(this.getContribuyente().getFechaDeNacimiento())
              .orElse(null));
      dto.setContribuyente(contribuyenteDTO);
    }
    */
    dto.setContribuyente(this.getContribuyente());

    dto.setEtiquetas(this.getEtiquetas().stream()
        .map(Etiqueta::getNombre)
        .collect(Collectors.toList()));

    dto.setContenidoMultimedia(this.getMultimedia().stream()
        .map(m -> {
          ContenidoMultimediaDTO dtoMultimedia = new ContenidoMultimediaDTO();
          dtoMultimedia.setPathArchivo(m.getPathArchivo());
          dtoMultimedia.setTipoContenido(m.getTipoContenido().name());
          return dtoMultimedia;
        }).collect(Collectors.toList()));

    dto.setTransicionesDeEstado(this.getTransiciones().stream()
        .map(t -> {
          TransicionEstadoHechoDTO tDto = new TransicionEstadoHechoDTO();
          tDto.setEstadoFinal(t.getEstadoFinal().name());
          tDto.setFecha(t.getFechaTransicion());
          return tDto;
        }).collect(Collectors.toList()));

    return dto;
  }
}