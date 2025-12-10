package ar.utn.ba.ddsi.agregador.dto.mapper;

import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.agregador.dto.ColeccionDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ColeccionMapper {

  private final HechoMapper hechoMapper;
  private final CriterioMapper criterioMapper;

  public ColeccionDTO toDTO(Coleccion coleccion) {
    if (coleccion == null) return null;

    ColeccionDTO dto = new ColeccionDTO();
    dto.setHandle(coleccion.getHandle());
    dto.setTitulo(coleccion.getTitulo());
    dto.setDescripcion(coleccion.getDescripcion());
    dto.setHechos(coleccion.getHechos() != null
        ? hechoMapper.toDTO(coleccion.getHechos())
        : List.of());
    dto.setHechosConsensuados(coleccion.getHechosConsensuados() != null
        ? hechoMapper.toDTO(coleccion.getHechosConsensuados())
        : List.of());
    if (coleccion.getCriterioDePertenencia() != null) {
      dto.setCriterioDePertenencia(
          criterioMapper.toDTO(coleccion.getCriterioDePertenencia())
      );
    }
    if (coleccion.getAlgoritmoConsenso() != null) {
      dto.setAlgoritmoConsenso(coleccion.getAlgoritmoConsenso().getNombre());
    }

    return dto;
  }

  public List<ColeccionDTO> toDTO(List<Coleccion> colecciones) {
    if (colecciones == null) return List.of();

    return colecciones.stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

}

