package ar.utn.ba.ddsi.dinamica.service;

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
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitudesService {
  private final ISolicitudesRepository solicitudesRepository;
  private final FuenteService fuenteService;
  private final MultimediaStorageService multimediaStorageService;
  private final ObjectMapper objectMapper;

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
        .fechaDeAcontecimiento(input.getFechaDeAcontecimiento())
        .fechaDeCargaSolicitud(LocalDateTime.now())
        .multimedia(new ArrayList<>())
        .transiciones(new ArrayList<>())
        .build();

    // Validaciones de dominio
    solicitud.validarCoordenadas();
    solicitud.validarFechaNoFutura();

    if (input.getMultimedia() != null && !input.getMultimedia().isEmpty()) {
      List<ContenidoMultimedia> multimedia = input.getMultimedia().stream()
          .map(dto -> dto.toContenidoMultimedia(solicitud))
          .toList();
      solicitud.getMultimedia().addAll(multimedia);
    }

    solicitud.cambiarEstado(EstadoSolicitud.PENDIENTE);

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
      solicitud.setFechaDeAcontecimiento(inputDTO.getNuevoFechaDeAcontecimiento());
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
      solicitud.setFechaDeAcontecimiento(inputDTO.getNuevoFechaDeAcontecimiento());
    }

    if (inputDTO.getNuevoContenidoMultimedia() != null) {
      solicitud.getMultimedia().clear();
      List<ContenidoMultimedia> nuevaMultimedia = inputDTO.getNuevoContenidoMultimedia().stream()
          .map(dto -> dto.toContenidoMultimedia(solicitud))
          .toList();
      solicitud.getMultimedia().addAll(nuevaMultimedia);
    }

    EstadoSolicitud nuevoEstado = inputDTO.getNuevoEstadoSolicitud();
    log.info("Cambiando estado de solicitud ID: {} a {}", id, nuevoEstado);
    solicitud.cambiarEstado(nuevoEstado);

    SolicitudHecho solicitudActualizada = solicitudesRepository.save(solicitud);

    if (solicitud.getEstado() == EstadoSolicitud.ACEPTADO ||
        solicitud.getEstado() == EstadoSolicitud.ACEPTADO_MODIFICADO) {
      log.info("Creando hecho para solicitud aceptada ID: {}", id);
      fuenteService.crearHechoDesdeSolicitud(solicitudActualizada);
    }

    log.info("Revisión completada para solicitud ID: {}", id);
    return solicitudActualizada.toOutputDTO();
  }

  @Transactional
  public SolicitudHecho recibirSolicitudContribuyenteConMultipart(
      String solicitudJson,
      List<MultipartFile> archivos) throws IOException {
    
    SolicitudHechoInputContribuyenteDTO input = parseJsonToDTO(solicitudJson);
    
    // Procesar archivos multimedia: guardar físicamente y determinar tipo de contenido
    if (archivos != null && !archivos.isEmpty()) {
      List<ContenidoMultimediaInputDTO> multimediaList = new ArrayList<>();
      
      for (MultipartFile archivo : archivos) {
        if (!archivo.isEmpty()) {
          // Guardar archivo en disco compartido (valida formato y retorna nombre único)
          String nombreArchivoGuardado = multimediaStorageService.guardarArchivo(archivo);
          // Inferir tipo de contenido desde MIME type o extensión
          TipoContenido tipo = determinarTipoContenido(archivo.getContentType(), archivo.getOriginalFilename());
          
          ContenidoMultimediaInputDTO multimediaDTO = new ContenidoMultimediaInputDTO();
          multimediaDTO.setPathArchivo(nombreArchivoGuardado);
          multimediaDTO.setTipoContenido(tipo);
          
          multimediaList.add(multimediaDTO);
        }
      }
      
      input.setMultimedia(multimediaList);
    }

    return recibirSolicitudContribuyente(input);
  }

  private SolicitudHechoInputContribuyenteDTO parseJsonToDTO(String solicitudJson) throws IOException {
    try {
      return objectMapper.readValue(solicitudJson, SolicitudHechoInputContribuyenteDTO.class);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      log.error("Error parseando JSON de solicitud: {}", e.getMessage(), e);
      throw new IOException("Error parseando JSON de solicitud: " + e.getMessage(), e);
    }
  }

  private TipoContenido determinarTipoContenido(String contentType, String nombreArchivo) {
    // Primero intentar inferir desde MIME type (más confiable)
    if (contentType != null) {
      TipoContenido resultado = switch (contentType) {
        case "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp" -> TipoContenido.IMAGEN;
        case "video/mp4", "video/webm" -> TipoContenido.VIDEO;
        case "audio/mpeg", "audio/mp3", "audio/wav", "audio/ogg" -> TipoContenido.AUDIO;
        default -> null;
      };
      if (resultado != null) {
        return resultado;
      }

    }
    
    // Fallback: inferir por extensión del archivo si MIME type no está disponible
    if (nombreArchivo != null && nombreArchivo.contains(".")) {
      String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1).toLowerCase();
      TipoContenido resultado = switch (extension) {
        case "jpg", "jpeg", "png", "gif", "webp" -> TipoContenido.IMAGEN;
        case "mp4", "webm" -> TipoContenido.VIDEO;
        case "mp3", "wav", "ogg" -> TipoContenido.AUDIO;
        default -> null;
      };
      if (resultado != null) {
        return resultado;
      }
    }
    
    throw new IllegalArgumentException("Formato de archivo no permitido. Formatos permitidos: " +
        "Imágenes: jpg, jpeg, png, gif, webp | Videos: mp4, webm | Audio: mp3, wav, ogg");
  }
}

