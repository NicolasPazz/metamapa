package ar.utn.ba.ddsi.dinamica.service;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudHechoInputContribuyenteDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarAdminInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarUserInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;

import java.util.List;

public interface ISolicitudesService {
    SolicitudHecho recibirSolicitudContribuyente(SolicitudHechoInputContribuyenteDTO input);

    SolicitudOutputDTO obtenerSolicitud(Long id);

    SolicitudOutputDTO modificarSolicitud(Long idSolicitud, SolicitudModificarUserInputDTO inputDTO);

    List<SolicitudOutputDTO> buscarTodas();

    SolicitudOutputDTO revisionSolicitud(Long idSolicitud, SolicitudModificarAdminInputDTO inputDTO);
}