package ar.utn.ba.ddsi.agregador.dto.mapper;

import ar.utn.ba.ddsi.agregador.domain.fuente.AFuente;
import ar.utn.ba.ddsi.agregador.dto.FuenteDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FuenteMapper {

    public FuenteDTO toDTO(AFuente fuente) {
        if (fuente == null) {
            return null;
        }
        
        return FuenteDTO.builder()
            .id(fuente.getId())
            .nombre(fuente.getNombre())
            .build();
    }

    public List<FuenteDTO> toDTO(List<AFuente> fuentes) {
        if (fuentes == null) {
            return List.of();
        }
        
        return fuentes.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }
}

