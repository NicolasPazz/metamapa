package ar.utn.ba.ddsi.agregador.domain.dto.mapper;

import ar.utn.ba.ddsi.agregador.domain.hecho.Etiqueta;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class EtiquetaMapper {
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
}
