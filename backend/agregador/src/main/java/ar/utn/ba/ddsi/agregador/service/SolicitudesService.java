package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.SolicitudEliminacion;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.exception.ValidacionException;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.factory.SolicitudEliminacionFactory;
import ar.utn.ba.ddsi.agregador.dto.SolicitudEliminacionDTO;
import ar.utn.ba.ddsi.agregador.dto.SolicitudEliminacionInputDTO;
import ar.utn.ba.ddsi.agregador.dto.mapper.SolicitudMapper;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.agregador.repositorie.ISolicitudesRepository;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.EstadoSolicitud;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolicitudesService {
  private final ISolicitudesRepository solicitudesRepository;
  private final IHechosRepository hechosRepository;
  private final SolicitudEliminacionFactory solicitudEliminacionFactory;
  private final SolicitudMapper solicitudMapper;

  @Transactional
  public ResponseEntity<?> crearSolicitud(SolicitudEliminacionInputDTO solicitudInputDTO) {
    Long idHecho = solicitudInputDTO.getHechoId();
    SolicitudEliminacion solicitudEliminacion;

    try {

      if(idHecho != null) {
        Optional<Hecho> hechoOpt = hechosRepository.findById(idHecho);
        if (hechoOpt.isEmpty()) {
          return ResponseEntity.badRequest().body("No se encontró el hecho con ID " + idHecho);
        }
        Hecho hecho = hechoOpt.get();
        log.info("[SolicitudesService] Hecho encontrado localmente: {}", hecho.getId());
        solicitudEliminacion = solicitudEliminacionFactory.crearSolicitud(
            hecho, solicitudInputDTO.getTextoFundamentos(), solicitudInputDTO.getContribuyenteId(), null, null
        );
      } else if (solicitudInputDTO.getFuenteId() != null && solicitudInputDTO.getIdOrigen() != null) {
        solicitudEliminacion = solicitudEliminacionFactory.crearSolicitud(
            null, solicitudInputDTO.getTextoFundamentos(), solicitudInputDTO.getContribuyenteId(),
            solicitudInputDTO.getFuenteId(), solicitudInputDTO.getIdOrigen()
        );
      } else {
        return ResponseEntity.badRequest().body("Debe proporcionar un ID de hecho o una combinación de fuente_id e id_origen");
      }

      SolicitudEliminacion solicitud = solicitudesRepository.save(solicitudEliminacion);
      log.info("[SolicitudesService] Solicitud creada localmente con ID: {}", solicitud.getId());
      return ResponseEntity.ok(solicitudMapper.toDTO(solicitud));

    } catch (ValidacionException e) {

      log.warn("[SolicitudesService] Validación fallida al crear solicitud: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());

    } catch (Exception e) {

      log.error("[SolicitudesService] Error inesperado al crear solicitud: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la solicitud: " + e.getMessage());

    }
  }

  public Map<Long, Set<Long>> blacklist() {
    log.debug("[SolicitudesService] Generando blacklist de hechos externos eliminados");

    List<SolicitudEliminacion> aceptadas =
        solicitudesRepository.findByEstado(EstadoSolicitud.ACEPTADA);

    Map<Long, Set<Long>> blacklistMap = aceptadas.stream()
        .filter(s -> s.getFuenteId() != null && s.getIdOrigen() != null)
        .collect(Collectors.groupingBy(
            SolicitudEliminacion::getFuenteId,
            Collectors.mapping(
                SolicitudEliminacion::getIdOrigen,
                Collectors.toSet()
            )
        ));
    
    log.debug("[SolicitudesService] Blacklist generada con {} fuentes y {} hechos externos eliminados", 
        blacklistMap.size(), 
        blacklistMap.values().stream().mapToInt(Set::size).sum());
    
    return blacklistMap;
  }

  @Transactional
  public ResponseEntity<?> actualizarEstado(Long id, SolicitudEliminacionDTO dto) {
    if (id == null || dto == null) {
      return ResponseEntity.badRequest().body("Debe proporcionar un ID de solicitud y un DTO válido.");
    }
    if (dto.getEstado() == null) {
      return ResponseEntity.badRequest().body("Debe proporcionar un estado válido.");
    }

    EstadoSolicitud nuevoEstado = dto.getEstado();

    try {
      // Verificar si la solicitud existe y validar que no sea spam
      Optional<SolicitudEliminacion> solicitudOpt = solicitudesRepository.findById(id);
      if (solicitudOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la solicitud con ID " + id);
      }

      SolicitudEliminacion solicitud = solicitudOpt.get();

      // Solo se pueden procesar solicitudes en estado PENDIENTE
      if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
        log.warn("[SolicitudesService] Intento de cambiar estado de solicitud {} que ya está en estado {}",
            id, solicitud.getEstado());
        return ResponseEntity.badRequest().body(
            "Solo se pueden procesar solicitudes en estado PENDIENTE. Estado actual: " + solicitud.getEstado());
      }

      // Las solicitudes marcadas como spam no pueden ser aceptadas
      if (solicitud.getSpam() && nuevoEstado == EstadoSolicitud.ACEPTADA) {
        log.warn("[SolicitudesService] Intento de aceptar solicitud {} marcada como spam", id);
        return ResponseEntity.badRequest().body("No se puede aceptar una solicitud marcada como spam");
      }

      if (nuevoEstado == EstadoSolicitud.ACEPTADA) {
        this.aceptar(id);
        return ResponseEntity.ok("Solicitud aceptada");
      } else if (nuevoEstado == EstadoSolicitud.RECHAZADA) {
        this.rechazar(id);
        return ResponseEntity.ok("Solicitud rechazada");
      } else {
        return ResponseEntity.badRequest().body("Estado no permitido para actualización. Solo se permite ACEPTADA o RECHAZADA.");
      }
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERROR: " + e.getMessage());
    } catch (Exception e) {
      log.error("[SolicitudesService] Error al actualizar estado de solicitud {}: {}", id, e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERROR inesperado: " + e.getMessage());
    }
  }

  @Transactional
  public void aceptar(Long idSolicitud) {
    Optional<SolicitudEliminacion> solicitud = solicitudesRepository.findById(idSolicitud);
    if (solicitud.isEmpty()) {
      throw new IllegalArgumentException("No se encontró la solicitud con ID " + idSolicitud);
    }
    SolicitudEliminacion solicitudEncontrada = solicitud.get();

    solicitudEncontrada.aceptar();
    solicitudesRepository.save(solicitudEncontrada);

    log.info("[SolicitudesService] Solicitud {} aceptada.", idSolicitud);
  }

  @Transactional
  public void rechazar(Long idSolicitud) {
    Optional<SolicitudEliminacion> solicitud = solicitudesRepository.findById(idSolicitud);
    if (solicitud.isEmpty()) {
      throw new IllegalArgumentException("No se encontró la solicitud con ID " + idSolicitud);
    }
    SolicitudEliminacion solicitudEncontrada = solicitud.get();

    solicitudEncontrada.rechazar();
    solicitudesRepository.save(solicitudEncontrada);
    log.info("[SolicitudesService] Solicitud {} rechazada.", idSolicitud);
  }

  public ResponseEntity<List<SolicitudEliminacionDTO>> getAll() {
    List<SolicitudEliminacion> solicitudes = solicitudesRepository.findAll();
    log.info("[SolicitudesService] Encontradas {} solicitudes en la base de datos", solicitudes.size());
    
    List<SolicitudEliminacionDTO> dtos = solicitudes.stream()
        .map(solicitudMapper::toDTO)
        .toList();
    return ResponseEntity.ok(dtos);
  }
}