package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IContribuyentesRepository;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.agregador.domain.repositorie.ISolicitudesRepository;
import ar.utn.ba.ddsi.agregador.domain.solicitud.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.agregador.domain.solicitud.detector.DetectorDeSpam;
import ar.utn.ba.ddsi.agregador.service.ISolicitudesService;
import ar.utn.ba.ddsi.agregador.user.Contribuyente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SolicitudesService implements ISolicitudesService {
  private final ISolicitudesRepository solicitudesRepository;
  private final IHechosRepository hechosRepository;
  private final IContribuyentesRepository contribuyentesRepository;
  private final DetectorDeSpam detectorDeSpam;

  @Override
  public SolicitudOutputDTO crear(SolicitudInputDTO solicitudInputDTO) {
    Hecho hecho = hechosRepository.findById(solicitudInputDTO.getIdHecho());
    //Long contribuyente = contribuyentesRepository.findById(solicitudInputDTO.getIdContribuyente());

    SolicitudDeEliminacion solicitud = new SolicitudDeEliminacion(hecho, solicitudInputDTO.getTextoFundamentos(), solicitudInputDTO.getIdContribuyente());

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
      dto.setIdContribuyente(solicitud.getContribuyente());
    }
    return dto;
  }
}