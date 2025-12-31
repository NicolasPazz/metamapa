package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.SolicitudEliminacion;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.exception.ValidacionException;
import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.factory.SolicitudEliminacionFactory;
import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SolicitudesService {
  private final ISolicitudesRepository solicitudesRepository;
  private final IHechosRepository hechosRepository;
  private final SolicitudEliminacionFactory solicitudEliminacionFactory;
  private final SolicitudMapper solicitudMapper;
  private final FuentesService fuentesService;

  // Constructor con @Lazy para romper la dependencia circular
  public SolicitudesService(
      ISolicitudesRepository solicitudesRepository,
      IHechosRepository hechosRepository,
      SolicitudEliminacionFactory solicitudEliminacionFactory,
      SolicitudMapper solicitudMapper,
      @Lazy FuentesService fuentesService) {
    this.solicitudesRepository = solicitudesRepository;
    this.hechosRepository = hechosRepository;
    this.solicitudEliminacionFactory = solicitudEliminacionFactory;
    this.solicitudMapper = solicitudMapper;
    this.fuentesService = fuentesService;
  }

  @Transactional
  public ResponseEntity<?> crearSolicitud(SolicitudEliminacionInputDTO solicitudInputDTO) {
    Long idHecho = solicitudInputDTO.getHechoId();
    SolicitudEliminacion solicitudEliminacion;

    try {

      if(idHecho != null) {
        // 1.a. Hecho interno: buscar en BD por ID
        Optional<Hecho> hechoOpt = hechosRepository.findById(idHecho);
        if (hechoOpt.isEmpty()) {
          return ResponseEntity.badRequest().body("No se encontró el hecho con ID " + idHecho);
        }
        Hecho hecho = hechoOpt.get();
        log.info("[SolicitudesService] Hecho interno encontrado en BD: {}", hecho.getId());
        
        // Si el hecho local tiene fuenteId e idOrigen (fue importado de fuente externa),
        // incluirlos en la solicitud para que quede en la blacklist al aceptarse
        Long fuenteIdHecho = hecho.getFuenteId();
        Long idOrigenHecho = hecho.getIdOrigen();
        
        solicitudEliminacion = solicitudEliminacionFactory.crearSolicitud(
            hecho, solicitudInputDTO.getTextoFundamentos(), solicitudInputDTO.getContribuyenteId(), 
            fuenteIdHecho, idOrigenHecho
        );
      } else if (solicitudInputDTO.getFuenteId() != null && solicitudInputDTO.getIdOrigen() != null) {
        // 2.b. Hecho externo: buscar por fuente_id e id_origen
        Long fuenteId = solicitudInputDTO.getFuenteId();
        Long idOrigen = solicitudInputDTO.getIdOrigen();

        // 2.b.i. Primero buscar en BD (hechos externos ya persistidos)
        Optional<Hecho> hechoExternoOpt = hechosRepository.findByFuenteIdAndIdOrigen(fuenteId, idOrigen);

        if (hechoExternoOpt.isPresent()) {
          Hecho hechoExterno = hechoExternoOpt.get();
          log.info("[SolicitudesService] Hecho externo encontrado en BD: ID={}, fuente_id={}, id_origen={}",
              hechoExterno.getId(), fuenteId, idOrigen);
          solicitudEliminacion = solicitudEliminacionFactory.crearSolicitud(
              hechoExterno, solicitudInputDTO.getTextoFundamentos(), solicitudInputDTO.getContribuyenteId(),
              fuenteId, idOrigen
          );
        } else {
          // 2.b.ii. Si no está en BD, buscar en fuentes en tiempo real
          log.info("[SolicitudesService] Hecho externo no encontrado en BD. Buscando en fuentes en tiempo real...");
          boolean hechoExiste = verificarHechoExisteEnFuentesExternas(fuenteId, idOrigen);

          if (!hechoExiste) {
            log.warn("[SolicitudesService] Hecho externo no existe - fuente_id: {}, id_origen: {}", fuenteId, idOrigen);
            return ResponseEntity.badRequest().body(
                "No se encontró el hecho con fuente_id " + fuenteId + " e id_origen " + idOrigen);
          }

          log.info("[SolicitudesService] Hecho externo verificado en fuentes en tiempo real: fuente_id={}, id_origen={}",
              fuenteId, idOrigen);
          // Crear solicitud sin hecho persistido (solo con fuente_id e id_origen)
          solicitudEliminacion = solicitudEliminacionFactory.crearSolicitud(
              null, solicitudInputDTO.getTextoFundamentos(), solicitudInputDTO.getContribuyenteId(),
              fuenteId, idOrigen
          );
        }
      } else {
        return ResponseEntity.badRequest().body("Debe proporcionar un ID de hecho o una combinación de fuente_id e id_origen");
      }

      // Si es spam, no persistir y responder con error
      if (solicitudEliminacion.esSpam()) {
        log.warn("[SolicitudesService] Solicitud spam detectada - No se persiste");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Spam detectado: El contenido de la solicitud parece ser spam y ha sido rechazado automáticamente");
      }

      // Persistir la solicitud solo si no es spam
      SolicitudEliminacion solicitud = solicitudesRepository.save(solicitudEliminacion);

      // Responder con éxito
      log.info("[SolicitudesService] Solicitud creada exitosamente con ID: {}", solicitud.getId());
      return ResponseEntity.status(HttpStatus.CREATED).body(solicitudMapper.toDTO(solicitud));

    } catch (ValidacionException e) {

      log.warn("[SolicitudesService] Validación fallida al crear solicitud: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());

    } catch (Exception e) {

      log.error("[SolicitudesService] Error inesperado al crear solicitud: {}", e.getMessage(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear la solicitud: " + e.getMessage());

    }
  }

  public Map<Long, Set<Long>> blacklist() {
    // Obtener todas las solicitudes aceptadas (hechos eliminados)
    List<SolicitudEliminacion> aceptadas =
        solicitudesRepository.findByEstado(EstadoSolicitud.ACEPTADA);

    // Agrupar por fuenteId -> Set de idOrigen para consulta rápida
    // Estructura: {fuenteId: {idOrigen1, idOrigen2, ...}}
    Map<Long, Set<Long>> blacklistMap = aceptadas.stream()
        .filter(s -> s.getFuenteId() != null && s.getIdOrigen() != null)
        .collect(Collectors.groupingBy(
            SolicitudEliminacion::getFuenteId,
            Collectors.mapping(
                SolicitudEliminacion::getIdOrigen,
                Collectors.toSet()
            )
        ));

    return blacklistMap;
  }

  @Transactional
  @CacheEvict(value = {"hechosPrimeraPagina", "provincias", "categorias"}, allEntries = true)
  public ResponseEntity<?> actualizarEstado(Long id, SolicitudEliminacionDTO dto) {
    if (id == null || dto == null) {
      return ResponseEntity.badRequest().body("Debe proporcionar un ID de solicitud y un DTO válido.");
    }
    if (dto.getEstado() == null) {
      return ResponseEntity.badRequest().body("Debe proporcionar un estado válido.");
    }

    EstadoSolicitud nuevoEstado = dto.getEstado();

    try {
      // Verificar si la solicitud existe (con JOIN FETCH para cargar el hecho asociado)
      Optional<SolicitudEliminacion> solicitudOpt = solicitudesRepository.findByIdWithHecho(id);
      if (solicitudOpt.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la solicitud con ID " + id);
      }

      SolicitudEliminacion solicitud = solicitudOpt.get();
      log.info("[SolicitudesService] Solicitud {} cargada. Hecho asociado: {}", 
          id, solicitud.getHecho() != null ? solicitud.getHecho().getId() : "ninguno");

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
        this.aceptarInterno(solicitud);
        log.info("[SolicitudesService] Solicitud {} aceptada. Caché invalidada.", id);
        return ResponseEntity.ok("Solicitud aceptada");
      } else if (nuevoEstado == EstadoSolicitud.RECHAZADA) {
        this.rechazarInterno(solicitud);
        log.info("[SolicitudesService] Solicitud {} rechazada.", id);
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

  private boolean verificarHechoExisteEnFuentesExternas(Long fuenteId, Long idOrigen) {
    try {
      // Obtener todos los hechos visibles desde fuentes externas (tiempo real)
      ResponseEntity<List<HechoDTO>> response =
          fuentesService.getAllHechosVisibles(null, null, null, null, null, null);

      if (response.getBody() == null || response.getBody().isEmpty()) {
        return false;
      }

      // Buscar el hecho específico por fuente_id + id_origen en la lista obtenida
      boolean existe = response.getBody().stream()
          .anyMatch(hecho ->
              hecho.getFuenteId() != null && hecho.getFuenteId().equals(fuenteId) &&
              hecho.getIdOrigen() != null && hecho.getIdOrigen().equals(idOrigen)
          );

      return existe;

    } catch (Exception e) {
      log.error("[SolicitudesService] Error al verificar hecho en fuentes externas: {}", e.getMessage(), e);
      // En caso de error, asumir que no existe para evitar crear solicitudes huérfanas
      return false;
    }
  }

  /**
   * Método interno para aceptar una solicitud. 
   * Debe ser llamado desde actualizarEstado para que @CacheEvict funcione correctamente.
   */
  private void aceptarInterno(SolicitudEliminacion solicitud) {
    // Ocultar el hecho si existe en la BD
    Hecho hecho = solicitud.getHecho();
    if (hecho != null) {
      hecho.ocultar();
      hechosRepository.save(hecho);
      log.info("[SolicitudesService] Hecho {} ocultado. Estado actual: {}", hecho.getId(), hecho.getEstado());
    } else {
      log.warn("[SolicitudesService] La solicitud {} no tiene un hecho asociado en la BD. " +
          "Se agregará a la blacklist via fuenteId={}, idOrigen={}", 
          solicitud.getId(), solicitud.getFuenteId(), solicitud.getIdOrigen());
    }

    // Cambiar estado de la solicitud a ACEPTADA
    solicitud.transicionarEstado(EstadoSolicitud.ACEPTADA);
    solicitudesRepository.save(solicitud);
  }

  /**
   * Método interno para rechazar una solicitud.
   * Debe ser llamado desde actualizarEstado.
   */
  private void rechazarInterno(SolicitudEliminacion solicitud) {
    // Cambiar estado de la solicitud a RECHAZADA (el hecho permanece visible)
    solicitud.transicionarEstado(EstadoSolicitud.RECHAZADA);
    solicitudesRepository.save(solicitud);
  }

  public ResponseEntity<List<SolicitudEliminacionDTO>> getAll() {
    // Solo devolver solicitudes pendientes para el panel de admin
    List<SolicitudEliminacion> solicitudes = solicitudesRepository.findByEstado(EstadoSolicitud.PENDIENTE);
    log.info("[SolicitudesService] Encontradas {} solicitudes pendientes", solicitudes.size());
    
    List<SolicitudEliminacionDTO> dtos = solicitudes.stream()
        .map(solicitudMapper::toDTO)
        .toList();
    return ResponseEntity.ok(dtos);
  }
}