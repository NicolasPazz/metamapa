package ar.utn.ba.ddsi.agregador.dto.mapper;

import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.TransicionEstadoSolicitud;
import ar.utn.ba.ddsi.agregador.dto.TransicionEstadoSolicitudDTO;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class TransicionEstadoSolicitudMapper {

    public TransicionEstadoSolicitudDTO toDTO(TransicionEstadoSolicitud transicion) {
        if (transicion == null) return null;

        TransicionEstadoSolicitudDTO dto = new TransicionEstadoSolicitudDTO();
        dto.setEstadoFinal(transicion.getEstadoFinal());
        dto.setFecha(transicion.getFecha());
        return dto;
    }

    public List<TransicionEstadoSolicitudDTO> toDTO(List<TransicionEstadoSolicitud> transiciones) {
        if (transiciones == null) return List.of();

        return transiciones.stream()
            .map(this::toDTO)
            .toList();
    }
}

