package ar.utn.ba.ddsi.agregador.dto.mapper;

import ar.utn.ba.ddsi.agregador.domain.hecho.Categoria;
import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class HechoMapper {

    private final ContenidoMultimediaMapper contenidoMultimediaMapper;
    private final TransicionEstadoHechoMapper transicionEstadoHechoMapper;
    //private final ContribuyenteMapper contribuyenteMapper;
    private final EtiquetaMapper etiquetaMapper;

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
      dto.setOrigen(hecho.getOrigen());
      dto.setEstado(hecho.getEstado());
      dto.setContribuyenteId(hecho.getContribuyenteId());
      dto.setEtiquetas(hecho.getEtiquetas() != null
          ? etiquetaMapper.toDTO(hecho.getEtiquetas())
          : List.of());
      dto.setMultimedia(hecho.getMultimedia() != null
          ? contenidoMultimediaMapper.toDTO(hecho.getMultimedia())
          : List.of());
      dto.setTransiciones(hecho.getTransiciones() != null
          ? transicionEstadoHechoMapper.toDTO(hecho.getTransiciones())
          : List.of());
      dto.setCategoria(hecho.getCategoria() != null ? hecho.getCategoria().getNombre() : null);
      dto.setFuenteId(hecho.getFuenteId());
      dto.setIdOrigen(hecho.getIdOrigen());

      return dto;
    }

  public List<HechoDTO> toDTO(List<Hecho> hechos) {
    if (hechos == null) return List.of();

    return hechos.stream()
        .map(this::toDTO)
        .toList();
  }

  public Hecho fromDTO(HechoDTO dto) {
    if (dto == null) return null;

    Categoria categoria = null;
    if (dto.getCategoria() != null && !dto.getCategoria().trim().isEmpty()) {
      categoria = new Categoria(dto.getCategoria().trim());
    }

    if (dto.getFechaDeAcontecimiento() == null) {
      log.warn("[HechoMapper] DTO sin fecha_acontecimiento. idOrigen={}, fuenteId={}, titulo={}",
          dto.getIdOrigen(), dto.getFuenteId(), dto.getTitulo());
    }

    return Hecho.builder()
        .id(dto.getId())
        .titulo(dto.getTitulo())
        .descripcion(dto.getDescripcion())
        .latitud(dto.getLatitud())
        .longitud(dto.getLongitud())
        .fechaDeAcontecimiento(dto.getFechaDeAcontecimiento())
        .fechaDeCarga(dto.getFechaDeCarga()) // Usar la fecha provista por la fuente, si existe
        .origen(dto.getOrigen())
        .contribuyenteId(dto.getContribuyenteId())
        .categoria(categoria)
        .fuenteId(dto.getFuenteId())
        .idOrigen(dto.getIdOrigen())
        .build();
  }

  public List<Hecho> fromDTO(List<HechoDTO> dtos) {
    if (dtos == null) return List.of();

    return dtos.stream()
        .map(this::fromDTO)
        .toList();
  }
}
