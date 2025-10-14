package ar.utn.ba.ddsi.dinamica.service.impl;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.ContenidoMultimediaInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudHechoInputContribuyenteDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarAdminInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarUserInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.hecho.ContenidoMultimedia;
import ar.utn.ba.ddsi.dinamica.domain.hecho.TipoContenido;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.ISolicitudesRepository;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.EstadoSolicitud;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;
import ar.utn.ba.ddsi.dinamica.service.IFuenteService;
import ar.utn.ba.ddsi.dinamica.service.ISolicitudesService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class SolicitudesService implements ISolicitudesService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SolicitudesService.class);

  private final ISolicitudesRepository solicitudesRepository;
  private final IFuenteService fuenteService;

  @Override
  public List<SolicitudOutputDTO> buscarTodas() {
    List<SolicitudHecho> solicitudes = this.solicitudesRepository.findAll();
    return solicitudes
        .stream()
        .map(SolicitudHecho::toOutputDTO)
        .toList();
  }

  @Override
  @Transactional
  public SolicitudHecho recibirSolicitudContribuyente(SolicitudHechoInputContribuyenteDTO input) {
    SolicitudHecho solicitud = new SolicitudHecho();
    solicitud.setIdContribuyente(input.getIdContribuyente());
    solicitud.setTitulo(input.getTitulo());
    solicitud.setDescripcion(input.getDescripcion());
    solicitud.setCategoria(input.getCategoria());
    solicitud.setLatitud(input.getLatitud());
    solicitud.setLongitud(input.getLongitud());
    solicitud.setFechaDeAcontecimiento(LocalDate.parse(input.getFechaDeAcontecimiento()));

    List<ContenidoMultimediaInputDTO> multimedia = input.getMultimedia();
    List<ContenidoMultimedia> contenidoMultimedia = new ArrayList<>();
    if (multimedia != null) {
      for (ContenidoMultimediaInputDTO item : multimedia) {
        String tipoStr = item.getTipoContenido();
        if (tipoStr == null || tipoStr.isBlank()) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de contenido no puede estar vacío");
        }

        try {
          TipoContenido.valueOf(tipoStr.toUpperCase());
          contenidoMultimedia = multimedia
              .stream()
              .map(dto -> dto.toContenidoMultimedia(solicitud))
              .collect(Collectors.toCollection(ArrayList::new));

        } catch (IllegalArgumentException e) {
          throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST,
              "Tipo de contenido inválido: " + tipoStr + ". Los tipos válidos son: "
                  + Arrays.toString(TipoContenido.values())
          );
        }
      }
    }

    solicitud.getMultimedia().clear();
    solicitud.getMultimedia().addAll(contenidoMultimedia);
    solicitud.setFechaDeCargaSolicitud(LocalDateTime.now());
    solicitud.setEstado(EstadoSolicitud.PENDIENTE);
    //solicitud.setTransiciones(new ArrayList<>());

    return solicitudesRepository.save(solicitud);
  }

  @Override
  public SolicitudOutputDTO obtenerSolicitud(Long id) {
    //TODO: cuando se modelen admin y users validar que se cuente con los permisos
    Optional<SolicitudHecho> solicitud = solicitudesRepository.findById(id);
    if (solicitud.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada");
    }
    return solicitud.get().toOutputDTO();
  }

  @Override
  @Transactional
  public SolicitudOutputDTO modificarSolicitud(Long id, SolicitudModificarUserInputDTO inputDTO) {
    Optional<SolicitudHecho> optSolicitud = solicitudesRepository.findById(id);

    if (optSolicitud.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada");
    }

    SolicitudHecho solicitud = optSolicitud.get();

    if (solicitud.getIdContribuyente() == null
        || inputDTO.getIdContribuyente() == null
        || !solicitud.getIdContribuyente().equals(inputDTO.getIdContribuyente())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado para modificar esta solicitud");
    }

    LOGGER.debug("Estado actual de la solicitud: {}", solicitud.getEstado());
    if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
      LOGGER.warn("Intento de modificar solicitud en estado {}. Solo PENDIENTE permitido.", solicitud.getEstado());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo solicitudes en estado PENDIENTE pueden ser " +
          "modificadas");
    }

    // Validar que no hayan pasado más de 7 días desde la fecha de carga
    if (solicitud.getFechaDeCargaSolicitud().plusDays(7).isBefore(LocalDateTime.now())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La solicitud ya no puede ser modificada");
    }

    // Modificar solo si los campos no vienen nulos
    if (inputDTO.getNuevoTitulo() != null)
      solicitud.setTitulo(inputDTO.getNuevoTitulo());

    if (inputDTO.getNuevoDescripcion() != null)
      solicitud.setDescripcion(inputDTO.getNuevoDescripcion());

    if (inputDTO.getNuevoNombreCategoria() != null)
      solicitud.setCategoria(inputDTO.getNuevoNombreCategoria());

    if (inputDTO.getNuevoLatitud() != null)
      solicitud.setLatitud(inputDTO.getNuevoLatitud());

    if (inputDTO.getNuevoLongitud() != null)
      solicitud.setLongitud(inputDTO.getNuevoLongitud());

    // TODO: Validar formato de fecha
    if (inputDTO.getNuevoFechaDeAcontecimiento() != null)
      solicitud.setFechaDeAcontecimiento(LocalDate.parse(inputDTO.getNuevoFechaDeAcontecimiento()));

    List<ContenidoMultimediaInputDTO> nuevoMultimedia = inputDTO.getNuevoContenidoMultimedia();
    if (nuevoMultimedia != null) {
      for (ContenidoMultimediaInputDTO item : nuevoMultimedia) {
        String tipoStr = item.getTipoContenido();
        if (tipoStr == null || tipoStr.isBlank()) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de contenido no puede estar vacío");
        }

        try {
          TipoContenido.valueOf(tipoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
          throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST,
              "Tipo de contenido inválido: " + tipoStr + ". Tipos válidos: "
                  + Arrays.toString(TipoContenido.values())
          );
        }
      }

      solicitud.getMultimedia().clear();
      solicitud.getMultimedia().addAll(
          nuevoMultimedia.stream()
              .map(dto -> dto.toContenidoMultimedia(solicitud))
              .collect(Collectors.toCollection(ArrayList::new))
      );
    }

    solicitud.setEstado(EstadoSolicitud.PENDIENTE);

    // Agregar la transición de estado
        /*if (solicitud.getTransiciones() == null) {
            solicitud.setTransiciones(new ArrayList<>());
        }
        solicitud.getTransiciones().add(new TransicionEstadoSolicitud(EstadoSolicitud.PENDIENTE));*/

    // Guardar cambios
    solicitudesRepository.save(solicitud);

    return solicitud.toOutputDTO();
  }

  @Override
  @Transactional
  public SolicitudOutputDTO revisionSolicitud(Long id, SolicitudModificarAdminInputDTO inputDTO) {
    LOGGER.info("Iniciando revisión de solicitud con id: {}", id);
    Optional<SolicitudHecho> optSolicitud = solicitudesRepository.findById(id);

    if (optSolicitud.isEmpty()) {
      LOGGER.warn("Solicitud no encontrada para id: {}", id);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada");
    }

    SolicitudHecho solicitud = optSolicitud.get();

    LOGGER.debug("Estado actual de la solicitud: {}", solicitud.getEstado());
    if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
      LOGGER.warn("Intento de modificar solicitud en estado {} por admin. Solo PENDIENTE permitido.", solicitud.getEstado());
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo solicitudes en estado PENDIENTE pueden ser modificadas por admin");
    }

    if (inputDTO.getNuevoTitulo() != null) {
      LOGGER.info("Modificando título de la solicitud");
      solicitud.setTitulo(inputDTO.getNuevoTitulo());
    }

    if (inputDTO.getNuevoDescripcion() != null) {
      LOGGER.info("Modificando descripción de la solicitud");
      solicitud.setDescripcion(inputDTO.getNuevoDescripcion());
    }

    if (inputDTO.getNuevoNombreCategoria() != null) {
      LOGGER.info("Modificando categoría de la solicitud");
      solicitud.setCategoria(inputDTO.getNuevoNombreCategoria());
    }

    if (inputDTO.getNuevoLatitud() != null) {
      LOGGER.info("Modificando latitud de la solicitud");
      solicitud.setLatitud(inputDTO.getNuevoLatitud());
    }

    if (inputDTO.getNuevoLongitud() != null) {
      LOGGER.info("Modificando longitud de la solicitud");
      solicitud.setLongitud(inputDTO.getNuevoLongitud());
    }

    if (inputDTO.getNuevoFechaDeAcontecimiento() != null) {
      LOGGER.info("Modificando fecha de acontecimiento de la solicitud");
      solicitud.setFechaDeAcontecimiento(LocalDate.parse(inputDTO.getNuevoFechaDeAcontecimiento()));
    }

    List<ContenidoMultimediaInputDTO> nuevoMultimedia = inputDTO.getNuevoContenidoMultimedia();
    if (nuevoMultimedia != null) {
      LOGGER.info("Modificando contenido multimedia de la solicitud");
      for (ContenidoMultimediaInputDTO item : nuevoMultimedia) {
        String tipoStr = item.getTipoContenido();
        if (tipoStr == null || tipoStr.isBlank()) {
          LOGGER.error("Tipo de contenido multimedia vacío");
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El tipo de contenido no puede estar vacío");
        }

        try {
          TipoContenido.valueOf(tipoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
          LOGGER.error("Tipo de contenido inválido: {}", tipoStr);
          throw new ResponseStatusException(
              HttpStatus.BAD_REQUEST,
              "Tipo de contenido inválido: " + tipoStr + ". Tipos válidos: "
                  + Arrays.toString(TipoContenido.values())
          );
        }
      }

      solicitud.getMultimedia().clear();
      solicitud.getMultimedia().addAll(
          nuevoMultimedia.stream()
              .map(dto -> dto.toContenidoMultimedia(solicitud))
              .collect(Collectors.toCollection(ArrayList::new))
      );
    }

    String estadoStr = inputDTO.getNuevoEstadoSolicitud();
    if (estadoStr == null || estadoStr.isBlank()) {
      LOGGER.error("Estado de solicitud vacío en revisión");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado de la solicitud es requerido para la revisión");
    }

    EstadoSolicitud nuevoEstado;
    try {
      nuevoEstado = EstadoSolicitud.valueOf(estadoStr.toUpperCase());
    } catch (IllegalArgumentException ex) {
      LOGGER.error("Estado inválido: {}", estadoStr);
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "Estado inválido: " + estadoStr + ". Estados válidos: " + Arrays.toString(EstadoSolicitud.values()));
    }
    LOGGER.info("Cambiando estado de la solicitud a: {}", nuevoEstado);
    solicitud.setEstado(nuevoEstado);

    // Guardar cambios
    LOGGER.info("Guardando cambios en la solicitud con id: {}", id);
    solicitudesRepository.save(solicitud);

    if (nuevoEstado == EstadoSolicitud.ACEPTADO || nuevoEstado == EstadoSolicitud.ACEPTADO_MODIFICADO) {
      LOGGER.info("Creando hecho asociado para la solicitud aceptada con id: {}", id);
      fuenteService.crearHechoDesdeSolicitud(solicitud);
    }

    LOGGER.info("Revisión de solicitud finalizada para id: {}", id);
    return solicitud.toOutputDTO();
  }
}
