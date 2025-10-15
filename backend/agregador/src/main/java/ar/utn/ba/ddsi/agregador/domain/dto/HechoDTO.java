package ar.utn.ba.ddsi.agregador.domain.dto;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ContenidoMultimediaDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.TransicionEstadoHechoDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Categoria;
import ar.utn.ba.ddsi.agregador.domain.hecho.ContenidoMultimedia;
import ar.utn.ba.ddsi.agregador.domain.hecho.DataHecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.EstadoHecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.Etiqueta;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.OrigenHecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.TipoContenido;
import ar.utn.ba.ddsi.agregador.domain.hecho.TransicionEstadoHecho;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class HechoDTO {
  private Long id;
  private String origen;
  private String estado;
  private Long contribuyente; //   private ContribuyenteDTO contribuyente;
  private List<String> etiquetas;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDate fechaDeAcontecimiento;
  private LocalDateTime fechaDeCarga;
  private LocalDateTime ultimaModificacion;
  private List<ContenidoMultimediaDTO> contenidoMultimedia;
  private List<TransicionEstadoHechoDTO> transicionesDeEstado;

  public Hecho mapToHecho() {
    Hecho hecho = new Hecho();

    //hecho.setId(this.id);
    hecho.setContribuyente(this.contribuyente);

    // Cargar DataHecho
    DataHecho data = new DataHecho();
    data.setTitulo(this.titulo);
    data.setDescripcion(this.descripcion);
    data.setLatitud(this.latitud);
    data.setLongitud(this.longitud);
    data.setFechaDeAcontecimiento(this.fechaDeAcontecimiento);
    data.setFechaDeCarga(this.fechaDeCarga);
    hecho.setData(data);

    // Origen y Estado
    if (this.origen != null)
      hecho.setOrigen(OrigenHecho.valueOf(this.origen));
    if (this.estado != null)
      hecho.setEstado(EstadoHecho.valueOf(this.estado));

    // Categoría
    if (this.categoria != null)
      hecho.setCategoria(new Categoria(this.categoria));

    // Etiquetas
    if (this.etiquetas != null) {
      Set<Etiqueta> etq = this.etiquetas.stream()
          .map(Etiqueta::new)
          .collect(Collectors.toSet());
      hecho.setEtiquetas(etq);
    }

    // Contenido Multimedia
    if (this.contenidoMultimedia != null) {
      List<ContenidoMultimedia> multimedia = this.contenidoMultimedia.stream()
          .map(m -> new ContenidoMultimedia(
              m.getPathArchivo(),
              TipoContenido.valueOf(m.getTipoContenido())
          ))
          .collect(Collectors.toList());
      hecho.setMultimedia(multimedia);
    }

    // Transiciones
    if (this.transicionesDeEstado != null) {
      List<TransicionEstadoHecho> transiciones = this.transicionesDeEstado.stream()
          .map(t -> new TransicionEstadoHecho(
              EstadoHecho.valueOf(t.getEstadoFinal()),
              t.getFecha()
          ))
          .collect(Collectors.toList());
      hecho.setTransiciones(transiciones);
    }

    return hecho;
  }
}