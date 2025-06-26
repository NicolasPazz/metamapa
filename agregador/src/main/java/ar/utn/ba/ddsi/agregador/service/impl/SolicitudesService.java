package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.agregador.domain.fuente.IFuente;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IContribuyentesRepository;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.agregador.domain.repositorie.ISolicitudesRepository;
import ar.utn.ba.ddsi.agregador.domain.solicitud.EstadoSolicitud;
import ar.utn.ba.ddsi.agregador.domain.solicitud.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.agregador.domain.solicitud.validacion.detector.DetectorDeSpam;
import ar.utn.ba.ddsi.agregador.service.ISolicitudesService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolicitudesService implements ISolicitudesService {
  private final ISolicitudesRepository solicitudesRepository;
  private final IHechosRepository hechosRepository;
  private final IContribuyentesRepository contribuyentesRepository;
  private final DetectorDeSpam detectorDeSpam;
  private final IdService idService;
  private final FuentesService fuentesService;

  @Override
  public SolicitudOutputDTO crear(SolicitudInputDTO solicitudInputDTO) {
    Long idHechoUnico = solicitudInputDTO.getIdHecho();

    // Buscar el hecho localmente
    Hecho hecho = hechosRepository.findById(idHechoUnico);
    if (hecho != null) {
      SolicitudDeEliminacion solicitud = new SolicitudDeEliminacion(
          hecho, solicitudInputDTO.getTextoFundamentos(), solicitudInputDTO.getIdContribuyente()
      );
      solicitudesRepository.save(solicitud);
      return solicitud.toDTO();
    }

    // Si no existe localmente, derivar a la fuente externa
    Long idFuente = idService.getIdFuente(idHechoUnico);
    Long idOrigen = idService.getIdOrigen(idHechoUnico);
    IFuente fuente = fuentesService.getFuenteById(idFuente);

    log.info("[SolicitudesService] Creando solicitud de eliminación para el hecho con ID unico {} en la fuente con ID {} " +
        "(id de origen = {}).", idHechoUnico, idFuente, idOrigen);

    if (fuente == null) {
      throw new IllegalArgumentException("No se encontró la fuente correspondiente al hecho.");
    }

    solicitudInputDTO.setIdHecho(idOrigen);

    var respuesta = fuente.crearSolicitudDeEliminacion(solicitudInputDTO);
    if (respuesta.getBody() == null) {
      throw new IllegalArgumentException("La fuente no respondió correctamente a la solicitud.");
    }

    var body = respuesta.getBody();
    SolicitudOutputDTO dto = new SolicitudOutputDTO();
    dto.setId(body.getIdHecho());
    dto.setIdHecho(idHechoUnico);
    dto.setTextoFundamentos(body.getTextoFundamentos());
    dto.setFecha(LocalDate.now());
    dto.setEstado(EstadoSolicitud.PENDIENTE);
    dto.setIdContribuyente(body.getIdContribuyente());

    return dto;
  }
}