package ar.utn.ba.ddsi.agregador.domain.hecho;

import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.ContenidoMultimediaDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.TransicionEstadoHechoDTO;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.stream.Collectors;

import jakarta.persistence.*;
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

  @Embedded
  private DataHecho data;

  @Enumerated(EnumType.STRING)
  @Column(name="origen", nullable = false)
  private OrigenHecho origen;

  @Enumerated(EnumType.STRING)
  @Column(name="estado", nullable = false)
  private EstadoHecho estado;

  @Column(name="contribuyente_id", nullable = true)
  private Long contribuyente;  // private Contribuyente contribuyente;

  @OneToMany
  @JoinColumn(name = "etiqueta_id", referencedColumnName = "id")
  @Builder.Default
  private Set<Etiqueta> etiquetas = new HashSet<>();

  @OneToMany
  @JoinColumn(name = "multimedia_id", referencedColumnName = "id")
  @Builder.Default
  private List<ContenidoMultimedia> multimedia = new ArrayList<>();

  @ElementCollection
  @Builder.Default
  private List<TransicionEstadoHecho> transiciones = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "categoria_id", referencedColumnName = "id")
  private Categoria categoria;

  public Hecho(String titulo, String descripcion, Categoria categoria,
               LocalDate fechaDeAcontecimiento, double latitud, double longitud,
               List<ContenidoMultimedia> multimedia, Set<Etiqueta> etiquetas, Long contribuyente) {

    DataHecho dataHecho = new DataHecho();
    dataHecho.setTitulo(titulo);
    dataHecho.setDescripcion(descripcion);
    dataHecho.setFechaDeAcontecimiento(fechaDeAcontecimiento);
    dataHecho.setLatitud(latitud);
    dataHecho.setLongitud(longitud);

    this.data = dataHecho;
    this.categoria = categoria;
    this.multimedia = multimedia;
    this.etiquetas = etiquetas;
    this.contribuyente = contribuyente;

    if(contribuyente != null) { //TODO: corregir, si el contribuyente es anonimo esto seria null y la carga seria CONTRIBUYENTE
      this.origen = OrigenHecho.CONTRIBUYENTE;
    } else {
      this.origen = OrigenHecho.MANUAL;
    }
    transicionarEstado(EstadoHecho.VISIBLE);
  }

  public boolean esDuplicadoDe(Hecho otroHecho) {
    return this.getTitulo().equalsIgnoreCase(otroHecho.getTitulo());
  }

  public boolean esVisible() {
    // TODO: verificar que no haya una solicitud de eliminacion aceptada
    return this.estado == EstadoHecho.VISIBLE;
  }

  // TODO: Verificar que no existe la etiqueta [RESUELTO]
  public void agregarEtiqueta(String etiq) {

    if (etiq != null && !etiq.isBlank()) {
      Etiqueta nueva = new Etiqueta(etiq);
        this.etiquetas.add(nueva);
    }

  }

  public boolean contieneEtiqueta(String etiqueta) {
    return this.etiquetas.contains(new Etiqueta(etiqueta));
  }

  public boolean esDeCategoria(Categoria categoria) {
    return this.categoria.equals(categoria);
  }

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

  // TODO: revisar si no conviene hacer una clase en estos dos casos
  public List<Long> getIdEtiquetas() {
    List<Long> ids = new ArrayList<>();
    for (Etiqueta etiqueta : this.etiquetas) {
      ids.add(etiqueta.getId());
    }
    return ids;
  }
  public List<String> getMultimediaPaths() {
    List<String> paths = new ArrayList<>();
    for (ContenidoMultimedia multim : this.multimedia) {
      paths.add(multim.getPathArchivo());
    }
    return paths;
  }

  public String getTitulo() {
    return data.getTitulo();
  }

  public String getDescripcion() {
    return data.getDescripcion();
  }

  public Double getLatitud() {
    return data.getLatitud();
  }

  public Double getLongitud() {
    return data.getLongitud();
  }

  public LocalDate getFechaDeAcontecimiento() {
    return data.getFechaDeAcontecimiento();
  }

  public LocalDateTime getFechaDeCarga() {
    return data.getFechaDeCarga();
  }
}