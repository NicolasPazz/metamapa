package ar.utn.ba.ddsi.dinamica.service;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudHechoInputContribuyenteDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarAdminInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarUserInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;

import java.util.List;

public interface ISolicitudesService {
    public Long recibirSolicitudContribuyente(SolicitudHechoInputContribuyenteDTO input);

    public SolicitudOutputDTO obtenerSolicitud(Long id);

    public SolicitudOutputDTO modificarSolicitud(Long idSolicitud, SolicitudModificarUserInputDTO inputDTO);

    public List<SolicitudOutputDTO> buscarTodas();

    public SolicitudOutputDTO revisionSolicitud(Long idSolicitud, SolicitudModificarAdminInputDTO inputDTO);
}