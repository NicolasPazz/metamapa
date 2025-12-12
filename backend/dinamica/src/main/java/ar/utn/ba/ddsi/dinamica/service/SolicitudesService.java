package ar.utn.ba.ddsi.dinamica.service;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudHechoInputContribuyenteDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarAdminInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarUserInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.hecho.ContenidoMultimedia;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.ISolicitudesRepository;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.EstadoSolicitud;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitudesService {
  private final ISolicitudesRepository solicitudesRepository;
  private final FuenteService fuenteService;

  public List<SolicitudOutputDTO> buscarTodas() {
    log.debug("Buscando todas las solicitudes");
    List<SolicitudHecho> solicitudes = solicitudesRepository.findAll();
    log.info("Se encontraron {} solicitudes", solicitudes.size());

    return solicitudes.stream()
        .map(SolicitudHecho::toOutputDTO)
        .toList();
  }

  @Transactional
  public SolicitudHecho recibirSolicitudContribuyente(SolicitudHechoInputContribuyenteDTO input) {
    log.info("Recibiendo nueva solicitud de contribuyente ID: {}", input.getIdContribuyente());

    SolicitudHecho solicitud = SolicitudHecho.builder()
        .idContribuyente(input.getIdContribuyente())
        .titulo(input.getTitulo())
        .descripcion(input.getDescripcion())
        .categoria(input.getCategoria())
        .latitud(input.getLatitud())
        .longitud(input.getLongitud())
        .fechaDeAcontecimiento(LocalDate.parse(input.getFechaDeAcontecimiento()))
        .fechaDeCargaSolicitud(LocalDateTime.now())
        .estado(EstadoSolicitud.PENDIENTE)
        .multimedia(new ArrayList<>())
        .transiciones(new ArrayList<>())
        .build();

    if (input.getMultimedia() != null && !input.getMultimedia().isEmpty()) {
      List<ContenidoMultimedia> multimedia = input.getMultimedia().stream()
          .map(dto -> dto.toContenidoMultimedia(solicitud))
          .toList();
      solicitud.getMultimedia().addAll(multimedia);
    }

    SolicitudHecho solicitudGuardada = solicitudesRepository.save(solicitud);

    log.info("Solicitud creada con ID: {} - Contribuyente: {} ({})",
        solicitudGuardada.getId(),
        solicitudGuardada.getIdContribuyente(),
        solicitudGuardada.esAnonimo() ? "Anónimo" : "Registrado");

    return solicitudGuardada;
  }

  public SolicitudOutputDTO obtenerSolicitud(Long id) {
    log.debug("Obteniendo solicitud con ID: {}", id);

    SolicitudHecho solicitud = solicitudesRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + id));

    return solicitud.toOutputDTO();
  }

  @Transactional
  public SolicitudOutputDTO modificarSolicitud(Long id, SolicitudModificarUserInputDTO inputDTO) {
    log.info("Modificando solicitud ID: {} por contribuyente ID: {}", id, inputDTO.getIdContribuyente());

    SolicitudHecho solicitud = solicitudesRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + id));

    solicitud.validarModificacionPorUsuario(inputDTO.getIdContribuyente());

    if (inputDTO.getNuevoTitulo() != null) solicitud.setTitulo(inputDTO.getNuevoTitulo());
    if (inputDTO.getNuevoDescripcion() != null) solicitud.setDescripcion(inputDTO.getNuevoDescripcion());
    if (inputDTO.getNuevoNombreCategoria() != null) solicitud.setCategoria(inputDTO.getNuevoNombreCategoria());
    if (inputDTO.getNuevoLatitud() != null) solicitud.setLatitud(inputDTO.getNuevoLatitud());
    if (inputDTO.getNuevoLongitud() != null) solicitud.setLongitud(inputDTO.getNuevoLongitud());
    if (inputDTO.getNuevoFechaDeAcontecimiento() != null) {
      solicitud.setFechaDeAcontecimiento(LocalDate.parse(inputDTO.getNuevoFechaDeAcontecimiento()));
    }

    if (inputDTO.getNuevoContenidoMultimedia() != null) {
      solicitud.getMultimedia().clear();
      List<ContenidoMultimedia> nuevaMultimedia = inputDTO.getNuevoContenidoMultimedia().stream()
          .map(dto -> dto.toContenidoMultimedia(solicitud))
          .toList();
      solicitud.getMultimedia().addAll(nuevaMultimedia);
    }

    SolicitudHecho solicitudActualizada = solicitudesRepository.save(solicitud);
    log.info("Solicitud ID: {} modificada exitosamente", id);

    return solicitudActualizada.toOutputDTO();
  }

  @Transactional
  public SolicitudOutputDTO revisionSolicitud(Long id, SolicitudModificarAdminInputDTO inputDTO) {
    log.info("Revisión administrativa de solicitud ID: {}", id);

    SolicitudHecho solicitud = solicitudesRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + id));

    if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
      throw new IllegalStateException("Solo se pueden revisar solicitudes en estado PENDIENTE");
    }

    if (inputDTO.getNuevoTitulo() != null) solicitud.setTitulo(inputDTO.getNuevoTitulo());
    if (inputDTO.getNuevoDescripcion() != null) solicitud.setDescripcion(inputDTO.getNuevoDescripcion());
    if (inputDTO.getNuevoNombreCategoria() != null) solicitud.setCategoria(inputDTO.getNuevoNombreCategoria());
    if (inputDTO.getNuevoLatitud() != null) solicitud.setLatitud(inputDTO.getNuevoLatitud());
    if (inputDTO.getNuevoLongitud() != null) solicitud.setLongitud(inputDTO.getNuevoLongitud());
    if (inputDTO.getNuevoFechaDeAcontecimiento() != null) {
      solicitud.setFechaDeAcontecimiento(LocalDate.parse(inputDTO.getNuevoFechaDeAcontecimiento()));
    }

    if (inputDTO.getNuevoContenidoMultimedia() != null) {
      solicitud.getMultimedia().clear();
      List<ContenidoMultimedia> nuevaMultimedia = inputDTO.getNuevoContenidoMultimedia().stream()
          .map(dto -> dto.toContenidoMultimedia(solicitud))
          .toList();
      solicitud.getMultimedia().addAll(nuevaMultimedia);
    }

    try {
      EstadoSolicitud nuevoEstado = EstadoSolicitud.valueOf(inputDTO.getNuevoEstadoSolicitud().toUpperCase());
      log.info("Cambiando estado de solicitud ID: {} a {}", id, nuevoEstado);
      solicitud.cambiarEstado(nuevoEstado);
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException("Estado inválido: " + inputDTO.getNuevoEstadoSolicitud());
    }

    SolicitudHecho solicitudActualizada = solicitudesRepository.save(solicitud);

    if (solicitud.getEstado() == EstadoSolicitud.ACEPTADO ||
        solicitud.getEstado() == EstadoSolicitud.ACEPTADO_MODIFICADO) {
      log.info("Creando hecho para solicitud aceptada ID: {}", id);
      fuenteService.crearHechoDesdeSolicitud(solicitudActualizada);
    }

    log.info("Revisión completada para solicitud ID: {}", id);
    return solicitudActualizada.toOutputDTO();
  }
}

