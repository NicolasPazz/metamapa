package ar.utn.ba.ddsi.agregador.domain.hecho;

import ar.utn.ba.ddsi.agregador.domain.dto.ContribuyenteDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.ContenidoMultimediaDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.TransicionEstadoHechoDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.multimedia.ContenidoMultimedia;
import ar.utn.ba.ddsi.agregador.domain.user.Contribuyente;
import ar.utn.ba.ddsi.agregador.service.ContribuyenteService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class HechoMapper {

  private final ContribuyenteService contribuyenteService;

  public HechoDTO toDTO(Hecho hecho) {
    if (hecho == null) return null;

    HechoDTO dto = new HechoDTO();

    dto.setId(hecho.getId());
    dto.setTitulo(hecho.getTitulo());
    dto.setDescripcion(hecho.getDescripcion());
    dto.setLatitud(hecho.getLatitud());
    dto.setLongitud(hecho.getLongitud());
    dto.setFechaDeAcontecimiento(hecho.getFechaDeAcontecimiento());
    dto.setFechaDeCarga(hecho.getFechaDeCarga());
    dto.setOrigen(hecho.getOrigen() != null ? hecho.getOrigen().name() : null);
    dto.setEstado(hecho.getEstado() != null ? hecho.getEstado().name() : null);
    dto.setContribuyente(this.toDTO(hecho.getContribuyente()));
    dto.setEtiquetas(this.toDTO(hecho.getEtiquetas()));
    dto.setMultimedia(this.toDTOMultimedia(hecho.getMultimedia()));
    dto.setTransiciones(this.toDTOTransiciones(hecho.getTransiciones()));
    dto.setCategoria(hecho.getCategoria() != null ? hecho.getCategoria().getNombre() : null);

    return dto;
  }

  // Contribuyente
  public ContribuyenteDTO toDTO(Long contribuyenteId) {
    if (contribuyenteId == null) return null;

    Contribuyente c = contribuyenteService.findById(contribuyenteId);
    return this.toDTO(c);
  }

  public ContribuyenteDTO toDTO(Contribuyente c) {
    if (c == null) return null;

    return new ContribuyenteDTO(
        c.getId(),
        c.getNombre(),
        c.getApellido(),
        c.getFechaDeNacimiento()
    );
  }

  public String fromDTO(Contribuyente c) {
    return c != null ? c.getNombre() + " " + c.getApellido() : null;
  }

  // Etiquetas
  public List<String> toDTO(Set<Etiqueta> etiquetas) {
    if (etiquetas == null) return null;

    return etiquetas.stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

  public String toDTO(Etiqueta etiqueta) {
    return etiqueta != null ? etiqueta.getNombre() : null;
  }

  public String fromDTO(Etiqueta etiqueta) {
    return etiqueta != null ? etiqueta.getNombre() : null;
  }

  // Contenido Multimedia
  public List<ContenidoMultimediaDTO> toDTOMultimedia(List<ContenidoMultimedia> multimedia) {
    if (multimedia == null) return null;

    return multimedia.stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

  public ContenidoMultimediaDTO toDTO(ContenidoMultimedia m) {
    if (m == null) return null;

    ContenidoMultimediaDTO dto = new ContenidoMultimediaDTO();
    dto.setPathArchivo(m.getPathArchivo());
    dto.setTipoContenido(m.getTipoContenido().name());
    return dto;
  }

  public String fromDTO(ContenidoMultimedia m) {
    return m != null ? m.getPathArchivo() : null;
  }

  // Transiciones Estado Hecho
  public List<TransicionEstadoHechoDTO> toDTOTransiciones(List<TransicionEstadoHecho> transiciones) {
    if (transiciones == null) return null;

    return transiciones.stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

  public TransicionEstadoHechoDTO toDTO(TransicionEstadoHecho t) {
    if (t == null) return null;

    TransicionEstadoHechoDTO dto = new TransicionEstadoHechoDTO();
    dto.setEstadoFinal(t.getEstadoFinal().name());
    dto.setFecha(t.getFechaTransicion());
    return dto;
  }

  public String fromDTO(TransicionEstadoHecho t) {
    return t != null ? t.getEstadoFinal().name() : null;
  }

}
