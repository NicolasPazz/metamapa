package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositories.IContribuyentesRepository;
import ar.utn.ba.ddsi.agregador.domain.repositories.IHechosRepository;
import ar.utn.ba.ddsi.agregador.domain.repositories.ISolicitudesRepository;
import ar.utn.ba.ddsi.agregador.domain.solicitud.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.agregador.domain.solicitud.detector.DetectorDeSpam;
import ar.utn.ba.ddsi.agregador.service.ISolicitudesService;
import ar.utn.ba.ddsi.agregador.user.Contribuyente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolicitudesService implements ISolicitudesService {
  @Autowired
  private ISolicitudesRepository solicitudesRepository;
  @Autowired
  private IHechosRepository hechosRepository;
  @Autowired
  private IContribuyentesRepository contribuyentesRepository;
  @Autowired
  private DetectorDeSpam detectorDeSpam;

  @Override
  public SolicitudOutputDTO crear(SolicitudInputDTO solicitudInputDTO) {
    Hecho hecho = hechosRepository.findById(solicitudInputDTO.getIdHecho());
    Contribuyente contribuyente = contribuyentesRepository.findById(solicitudInputDTO.getIdContribuyente());

    SolicitudDeEliminacion solicitud = new SolicitudDeEliminacion(hecho, solicitudInputDTO.getTextoFundamentos(), contribuyente);

    if (detectorDeSpam.esSpam(solicitud.getTextoFundamentos())) {
      solicitud.rechazar();
      throw new IllegalArgumentException("La solicitud fue rechazada por contener spam.");
    }

    solicitudesRepository.save(solicitud);
    return solicitudOutputDTO(solicitud);
  }

  private SolicitudOutputDTO solicitudOutputDTO(SolicitudDeEliminacion solicitud) {
    SolicitudOutputDTO dto = new SolicitudOutputDTO();
    dto.setId(solicitud.getId());
    dto.setIdHecho(solicitud.getHecho().getId());
    dto.setTextoFundamentos(solicitud.getTextoFundamentos());
    dto.setFecha(solicitud.getFecha());
    dto.setEstado(solicitud.getEstado());
    if (solicitud.getContribuyente() != null) {
      dto.setIdContribuyente(solicitud.getContribuyente().getId());
    }
    return dto;
  }
}