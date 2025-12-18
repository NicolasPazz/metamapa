package ar.utn.ba.ddsi.agregador.dto.mapper;

import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.SolicitudEliminacion;
import ar.utn.ba.ddsi.agregador.dto.SolicitudEliminacionDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SolicitudMapper {

  private final HechoMapper hechoMapper;
  private final TransicionEstadoSolicitudMapper transicionMapper;

  public SolicitudEliminacionDTO toDTO(SolicitudEliminacion solicitud) {
    if (solicitud == null) return null;

    SolicitudEliminacionDTO dto = new SolicitudEliminacionDTO();
    dto.setId(solicitud.getId());
    dto.setHecho(hechoMapper.toDTO(solicitud.getHecho()));
    dto.setTextoFundamentos(solicitud.getTextoFundamentos());
    dto.setFecha(solicitud.getFecha());
    dto.setEstado(solicitud.getEstado());
    dto.setContribuyenteId(solicitud.getContribuyenteId());
    dto.setSpam(solicitud.getSpam()); // Mapear el campo spam
    dto.setFuenteId(solicitud.getFuenteId());
    dto.setIdOrigen(solicitud.getIdOrigen());

    if (solicitud.getTransiciones() != null) {
      dto.setTransiciones(transicionMapper.toDTO(solicitud.getTransiciones()));
    }

    return dto;
  }
}

