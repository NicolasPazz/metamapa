package ar.utn.ba.ddsi.agregador.dto.mapper;

import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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

      return dto;
    }

  public List<HechoDTO> toDTO(List<Hecho> hechos) {
    if (hechos == null) return List.of();

    return hechos.stream()
        .map(this::toDTO)
        .toList();
  }
}
