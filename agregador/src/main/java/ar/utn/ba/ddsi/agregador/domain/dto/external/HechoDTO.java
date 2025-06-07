package ar.utn.ba.ddsi.agregador.domain.dto.external;

import ar.utn.ba.ddsi.agregador.domain.hecho.Categoria;
import ar.utn.ba.ddsi.agregador.domain.hecho.ContenidoMultimedia;
import ar.utn.ba.ddsi.agregador.domain.hecho.DataHecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.EstadoHecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.Etiqueta;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.OrigenHecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.TipoContenido;
import ar.utn.ba.ddsi.agregador.domain.hecho.TransicionEstadoHecho;
import ar.utn.ba.ddsi.agregador.user.Contribuyente;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class HechoDTO {
  private Long id;
  private String origen;
  private String estado;
  private ContribuyenteDTO contribuyente;
  private List<String> etiquetas;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private String fechaDeAcontecimiento;
  private String fechaDeCarga;
  private String ultimaModificacion;
  private List<ContenidoMultimediaDTO> contenidoMultimedia;
  private List<TransicionEstadoHechoDTO> transicionesDeEstado;

  public Hecho mapToHecho() {
    Hecho hecho = new Hecho();

    hecho.setId(this.getId());

    DataHecho data = new DataHecho();
    data.setTitulo(this.getTitulo());
    data.setDescripcion(this.getDescripcion());
    data.setLatitud(this.getLatitud());
    data.setLongitud(this.getLongitud());
    data.setFechaDeAcontecimiento(
        Instant.parse(this.getFechaDeAcontecimiento())
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    );
    data.setFechaDeCarga(
        Instant.parse(this.getFechaDeCarga())
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    );
    data.setNombreCategoria(this.getCategoria());
    hecho.setData(data);

    hecho.setOrigen(OrigenHecho.valueOf(this.getOrigen()));
    hecho.setEstado(EstadoHecho.valueOf(this.getEstado()));
    hecho.setCategoria(new Categoria(this.getCategoria()));

    if (this.getContribuyente() != null) {
      Contribuyente cont = new Contribuyente();
      cont.setNombre(this.getContribuyente().getNombre());
      cont.setApellido(this.getContribuyente().getApellido());
      if (this.getContribuyente().getFechaDeNacimiento() != null) {
        cont.setFechaDeNacimiento(LocalDate.parse(this.getContribuyente().getFechaDeNacimiento()));
      }
      hecho.setContribuyente(cont);
    }

    if (this.getEtiquetas() != null) {
      Set<Etiqueta> etq = this.getEtiquetas().stream()
          .map(Etiqueta::new)
          .collect(Collectors.toSet());
      hecho.setEtiquetas(etq);
    }

    if (this.getContenidoMultimedia() != null) {
      List<ContenidoMultimedia> multimedia = this.getContenidoMultimedia().stream()
          .map(m -> new ContenidoMultimedia(
              m.getPathArchivo(),
              TipoContenido.valueOf(m.getTipoContenido())
          ))
          .collect(Collectors.toList());
      hecho.setMultimedia(multimedia);
    }

    if (this.getTransicionesDeEstado() != null) {
      List<TransicionEstadoHecho> transiciones = this.getTransicionesDeEstado().stream()
          .map(t -> new TransicionEstadoHecho(
              EstadoHecho.valueOf(t.getEstadoFinal()),
              LocalDate.parse(t.getFecha())
          ))
          .collect(Collectors.toList());
      hecho.setTransiciones(transiciones);
    }

    return hecho;
  }
}