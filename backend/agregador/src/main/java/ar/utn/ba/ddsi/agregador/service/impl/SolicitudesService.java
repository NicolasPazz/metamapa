package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.agregador.domain.fuente.IFuente;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.agregador.domain.repositorie.ISolicitudesRepository;
import ar.utn.ba.ddsi.agregador.domain.solicitud.EstadoSolicitud;
import ar.utn.ba.ddsi.agregador.domain.solicitud.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.agregador.domain.solicitud.validacion.Validacion;
import ar.utn.ba.ddsi.agregador.domain.solicitud.validacion.ValidacionLongitud;
import ar.utn.ba.ddsi.agregador.domain.solicitud.validacion.detector.DetectorDeSpamTFIDF;
import ar.utn.ba.ddsi.agregador.service.ISolicitudesService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolicitudesService implements ISolicitudesService {
  private final ISolicitudesRepository solicitudesRepository;
  private final IHechosRepository hechosRepository;
  private final IdService idService;
  private final FuentesService fuentesService;
  private final List<Validacion> validaciones = List.of(
      new ValidacionLongitud(50),
      new DetectorDeSpamTFIDF()
  );

  @Override
  public boolean esValida(String textoFundamentos) {
    return validaciones.stream().allMatch(validacion -> validacion.esValida(textoFundamentos));
  }

  private String truncarTextoFundamentos(String texto) {
    if (texto != null && texto.length() > 2000) {
      log.warn("[SolicitudesService] Texto de fundamentos truncado por exceder 2000 caracteres");
      return texto.substring(0, 1997) + "...";
    }
    return texto;
  }

  @Override
  @Transactional
  public ResponseEntity<?> crear(SolicitudInputDTO solicitudInputDTO) {
    if (!esValida(solicitudInputDTO.getTextoFundamentos())) {
      return ResponseEntity.badRequest().body("El texto de fundamentos no cumplió con las validaciones.");
    }
    Long idHechoUnico = solicitudInputDTO.getIdHecho();

    // Buscar el hecho localmente
    log.info("[SolicitudesService] Buscando hecho con ID único: {}", idHechoUnico);
    Optional<Hecho> hecho = hechosRepository.findById(idHechoUnico);
    if (hecho.isPresent()) {
      Hecho hechoEncontrado = hecho.get();
      log.info("[SolicitudesService] Hecho encontrado localmente: {}", hechoEncontrado.getId());
      
      // Truncar el texto si es muy largo para evitar errores de BD
      String textoTruncado = truncarTextoFundamentos(solicitudInputDTO.getTextoFundamentos());
      
      SolicitudDeEliminacion solicitud = new SolicitudDeEliminacion(
          hechoEncontrado, textoTruncado, solicitudInputDTO.getIdContribuyente()
      );
      SolicitudDeEliminacion solicitudGuardada = solicitudesRepository.save(solicitud);
      log.info("[SolicitudesService] Solicitud creada localmente con ID: {}", solicitudGuardada.getId());
      return ResponseEntity.ok(solicitudGuardada.toDTO());
    } else {
      log.info("[SolicitudesService] Hecho con ID {} no encontrado localmente, derivando a fuente externa", idHechoUnico);
    }

    // Si no existe localmente, derivar a la fuente externa
    Long idFuente = idService.getIdFuente(idHechoUnico);
    Long idOrigen = idService.getIdOrigen(idHechoUnico);
    IFuente fuente = fuentesService.getFuenteById(idFuente);

    log.info("[SolicitudesService] Creando solicitud de eliminación para el hecho con ID unico {} en la fuente con ID {} " +
        "(id de origen = {}).", idHechoUnico, idFuente, idOrigen);

    if (fuente == null) {
      return ResponseEntity.badRequest().body("No se encontró la fuente correspondiente al hecho.");
    }

    solicitudInputDTO.setIdHecho(idOrigen);

    var respuesta = fuente.crearSolicitudDeEliminacion(solicitudInputDTO);
    if (respuesta.getBody() == null) {
      return ResponseEntity.status(502).body("La fuente no respondió correctamente a la solicitud.");
    }

    SolicitudOutputDTO dto = respuesta.getBody();
    dto.setIdHecho(idHechoUnico);

    // Guardar la solicitud localmente
    try {
      // Truncar el texto si es muy largo para evitar errores de BD
      String textoTruncado = truncarTextoFundamentos(solicitudInputDTO.getTextoFundamentos());
      
      SolicitudDeEliminacion solicitudLocal = new SolicitudDeEliminacion(
          null, // No hay hecho local
          textoTruncado,
          solicitudInputDTO.getIdContribuyente()
      );
      solicitudLocal.setHechoId(idHechoUnico);
      SolicitudDeEliminacion solicitudGuardada = solicitudesRepository.save(solicitudLocal);
      log.info("[SolicitudesService] Solicitud de fuente externa guardada localmente con ID: {} (ID externo: {})", 
               solicitudGuardada.getId(), dto.getId());
    } catch (Exception e) {
      log.warn("[SolicitudesService] No se pudo guardar la solicitud localmente: {}", e.getMessage());
    }

    return ResponseEntity.ok(dto);
  }

  @Override
  @Transactional
  public ResponseEntity<?> actualizarEstado(Long id, SolicitudOutputDTO dto) {
    if (id == null || dto == null) {
      return ResponseEntity.badRequest().body("Debe proporcionar un ID de solicitud y un DTO válido.");
    }
    if (dto.getEstado() == null) {
      return ResponseEntity.badRequest().body("Debe proporcionar un estado válido.");
    }

    EstadoSolicitud nuevoEstado = dto.getEstado();

    try {
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

  @Override
  @Transactional
  public void aceptar(Long idSolicitud) {
    Optional<SolicitudDeEliminacion> solicitud = solicitudesRepository.findById(idSolicitud);
    if (solicitud.isEmpty()) {
      throw new IllegalArgumentException("No se encontró la solicitud con ID " + idSolicitud);
    }
    SolicitudDeEliminacion solicitudEncontrada = solicitud.get();

    solicitudEncontrada.aceptar();
    solicitudesRepository.save(solicitudEncontrada);
    hechosRepository.ocultar(solicitudEncontrada.getHechoId());
    log.info("[SolicitudesService] Solicitud {} aceptada.", idSolicitud);
  }

  @Override
  @Transactional
  public void rechazar(Long idSolicitud) {
    Optional<SolicitudDeEliminacion> solicitud = solicitudesRepository.findById(idSolicitud);
    if (solicitud.isEmpty()) {
      throw new IllegalArgumentException("No se encontró la solicitud con ID " + idSolicitud);
    }
    SolicitudDeEliminacion solicitudEncontrada = solicitud.get();

    solicitudEncontrada.rechazar();
    solicitudesRepository.save(solicitudEncontrada);
    log.info("[SolicitudesService] Solicitud {} rechazada.", idSolicitud);
  }

  public ResponseEntity<List<SolicitudOutputDTO>> getAll() {
    List<SolicitudDeEliminacion> solicitudes = solicitudesRepository.findAll();
    log.info("[SolicitudesService] Encontradas {} solicitudes en la base de datos", solicitudes.size());
    
    // Debug: verificar hechos locales
    List<Hecho> hechosLocales = hechosRepository.findAll();
    log.info("[SolicitudesService] Hechos locales en BD: {}", hechosLocales.size());
    hechosLocales.forEach(h -> log.info("[SolicitudesService] Hecho local ID: {}", h.getId()));
    
    List<SolicitudOutputDTO> dtos = solicitudes.stream()
        .map(SolicitudDeEliminacion::toDTO)
        .toList();
    return ResponseEntity.ok(dtos);
  }
}