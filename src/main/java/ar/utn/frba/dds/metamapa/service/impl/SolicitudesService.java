package ar.utn.frba.dds.metamapa.service.impl;

import ar.utn.frba.dds.metamapa.domain.dto.input.SolicitudInputDTO;
import ar.utn.frba.dds.metamapa.domain.dto.output.SolicitudOutputDTO;
import ar.utn.frba.dds.metamapa.domain.hecho.Hecho;
import ar.utn.frba.dds.metamapa.domain.repositorie.IContribuyentesRepository;
import ar.utn.frba.dds.metamapa.domain.repositorie.IHechosRepository;
import ar.utn.frba.dds.metamapa.domain.repositorie.ISolicitudesRepository;
import ar.utn.frba.dds.metamapa.domain.request.SolicitudDeEliminacion;
import ar.utn.frba.dds.metamapa.domain.user.Contribuyente;
import ar.utn.frba.dds.metamapa.service.DetectorDeSpam;
import ar.utn.frba.dds.metamapa.service.ISolicitudesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SolicitudesService implements ISolicitudesService {
  @Autowired
  private ISolicitudesRepository solicitudRepository;
  @Autowired
  private IHechosRepository hechoRepository;
  @Autowired
  private IContribuyentesRepository contribuyenteRepository;
  @Autowired
  private DetectorDeSpam detectorDeSpam;

  @Override
  public SolicitudOutputDTO crear(SolicitudInputDTO solicitudInputDTO) {
    Hecho hecho = hechoRepository.findById(solicitudInputDTO.getIdHecho());
    Contribuyente contribuyente = contribuyenteRepository.findById(solicitudInputDTO.getIdContribuyente());

    SolicitudDeEliminacion solicitud = new SolicitudDeEliminacion(hecho, solicitudInputDTO.getTextoFundamentos(), contribuyente, solicitudInputDTO.getValidaciones());

    if (detectorDeSpam.esSpam(solicitud.getTextoFundamentos())) {
      solicitud.rechazar();
    }

    solicitudRepository.save(solicitud);
    return solicitudOutputDTO(solicitud);
  }

  private SolicitudOutputDTO solicitudOutputDTO(SolicitudDeEliminacion solicitud) {
    SolicitudOutputDTO dto = new SolicitudOutputDTO();
    dto.setId(solicitud.getId());
    dto.setIdHecho(solicitud.getHecho().getId());
    dto.setTextoFundamentos(solicitud.getTextoFundamentos());
    dto.setFecha(solicitud.getFecha());
    dto.setEstado(solicitud.getEstado());
    dto.setIdContribuyente(solicitud.getContribuyente().getId());
    return dto;
  }
}