package ar.utn.ba.ddsi.agregador.dto.mapper;

import ar.utn.ba.ddsi.agregador.dto.TransicionEstadoHechoDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.TransicionEstadoHecho;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TransicionEstadoHechoMapper {
  public List<TransicionEstadoHechoDTO> toDTO(List<TransicionEstadoHecho> transiciones) {
    if (transiciones == null) return null;

    return transiciones.stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
  }

  public TransicionEstadoHechoDTO toDTO(TransicionEstadoHecho t) {
    if (t == null) return null;

    TransicionEstadoHechoDTO dto = new TransicionEstadoHechoDTO();
    dto.setEstadoFinal(t.getEstadoFinal());
    dto.setFecha(t.getFechaTransicion());
    return dto;
  }

  public String fromDTO(TransicionEstadoHecho t) {
    return t != null ? t.getEstadoFinal().name() : null;
  }
}
