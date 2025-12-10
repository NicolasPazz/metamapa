package ar.utn.ba.ddsi.agregador.dto.mapper;

import ar.utn.ba.ddsi.agregador.dto.ContenidoMultimediaDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.multimedia.ContenidoMultimedia;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ContenidoMultimediaMapper {
  public List<ContenidoMultimediaDTO> toDTO(List<ContenidoMultimedia> multimedia) {
    if (multimedia == null) return null;

    return multimedia.stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

  public ContenidoMultimediaDTO toDTO(ContenidoMultimedia m) {
    if (m == null) return null;

    ContenidoMultimediaDTO dto = new ContenidoMultimediaDTO();
    dto.setPathArchivo(m.getPathArchivo());
    dto.setTipoContenido(m.getTipoContenido());
    return dto;
  }

  public String fromDTO(ContenidoMultimedia m) {
    return m != null ? m.getPathArchivo() : null;
  }
}
