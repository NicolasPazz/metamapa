package ar.utn.ba.ddsi.dinamica.service;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.ContenidoMultimediaInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudHechoInputContribuyenteDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarAdminInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarUserInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.hecho.ContenidoMultimedia;
import ar.utn.ba.ddsi.dinamica.domain.hecho.TipoContenido;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.IHechosRepository;
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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitudesService {
  private final ISolicitudesRepository solicitudesRepository;
  private final IHechosRepository hechosRepository;
  private final FuenteService fuenteService;
  private final MultimediaStorageService multimediaStorageService;
  private final ObjectMapper objectMapper;
  private final WebClient webClientAgregador;

  public List<SolicitudOutputDTO> buscarTodas() {
    log.debug("Buscando todas las solicitudes");
    List<SolicitudHecho> solicitudes = solicitudesRepository.findAll();
    log.debug("Se encontraron {} solicitudes", solicitudes.size());

    return solicitudes.stream()
        .map(s -> agregarIdHecho(s.toOutputDTO(), s.getId()))
        .toList();
  }

  public List<SolicitudOutputDTO> buscarPorContribuyente(Long idContribuyente) {
    log.debug("Buscando solicitudes del contribuyente ID: {}", idContribuyente);
    List<SolicitudHecho> solicitudes = solicitudesRepository.findByIdContribuyente(idContribuyente);
    log.debug("Se encontraron {} solicitudes para el contribuyente {}", solicitudes.size(), idContribuyente);

    return solicitudes.stream()
        .map(s -> agregarIdHecho(s.toOutputDTO(), s.getId()))
        .toList();
  }

  @Transactional
  public SolicitudHecho recibirSolicitudContribuyente(SolicitudHechoInputContribuyenteDTO input) {
    log.debug("SolicitudesService.recibirSolicitudContribuyente - Iniciando");
    log.debug("SolicitudesService.recibirSolicitudContribuyente - Nueva solicitud de contribuyente ID: {}", input.getIdContribuyente());
    log.debug("SolicitudesService.recibirSolicitudContribuyente - Título: {}, Latitud: {}, Longitud: {}, Categoría: {}", 
            input.getTitulo(), input.getLatitud(), input.getLongitud(), input.getCategoria());

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

    log.debug("Solicitud creada con ID: {} - Contribuyente: {} ({})",
        solicitudGuardada.getId(),
        solicitudGuardada.getIdContribuyente(),
        solicitudGuardada.esAnonimo() ? "Anónimo" : "Registrado");

    return solicitudGuardada;
  }

  public SolicitudOutputDTO obtenerSolicitud(Long id) {
    log.debug("Obteniendo solicitud con ID: {}", id);

    SolicitudHecho solicitud = solicitudesRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + id));

    return agregarIdHecho(solicitud.toOutputDTO(), id);
  }

  @Transactional
  public SolicitudOutputDTO modificarSolicitudConMultipart(
      Long id, String solicitudJson, List<MultipartFile> archivos) throws IOException {
    
    log.debug("Modificando solicitud ID: {} con multipart (igual que en la creación)", id);
    log.debug("SolicitudesService.modificarSolicitudConMultipart - JSON recibido: {}", solicitudJson);
    
    // Parsear el JSON igual que en la creación
    SolicitudModificarUserInputDTO inputDTO = parseJsonToModificarDTO(solicitudJson);
    
    // Procesar archivos multimedia igual que en la creación
    if (archivos != null && !archivos.isEmpty()) {
      List<ContenidoMultimediaInputDTO> multimediaList = new ArrayList<>();
      for (MultipartFile archivo : archivos) {
        if (!archivo.isEmpty()) {
          String nombreArchivoGuardado = multimediaStorageService.guardarArchivo(archivo);
          TipoContenido tipo = determinarTipoContenido(archivo.getContentType(), archivo.getOriginalFilename());
          ContenidoMultimediaInputDTO multimediaDTO = new ContenidoMultimediaInputDTO();
          multimediaDTO.setPathArchivo(nombreArchivoGuardado);
          multimediaDTO.setTipoContenido(tipo);
          multimediaList.add(multimediaDTO);
        }
      }
      inputDTO.setNuevoContenidoMultimedia(multimediaList);
    }
    
    return modificarSolicitud(id, inputDTO);
  }

  private SolicitudModificarUserInputDTO parseJsonToModificarDTO(String solicitudJson) throws IOException {
    try {
      return objectMapper.readValue(solicitudJson, SolicitudModificarUserInputDTO.class);
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
      log.error("Error parseando JSON de solicitud modificación: {}", e.getMessage(), e);
      throw new IOException("Error parseando JSON de solicitud: " + e.getMessage(), e);
    }
  }

  @Transactional
  public SolicitudOutputDTO modificarSolicitud(Long id, SolicitudModificarUserInputDTO inputDTO) {
    log.debug("Modificando solicitud ID: {} por contribuyente ID: {}", id, inputDTO.getIdContribuyente());

    SolicitudHecho solicitud = solicitudesRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + id));

    solicitud.validarModificacionPorUsuario(inputDTO.getIdContribuyente());

    // Guardar el estado anterior para verificar si estaba aceptada
    EstadoSolicitud estadoAnterior = solicitud.getEstado();

    // Si la solicitud estaba en ACEPTADO o ACEPTADO_MODIFICADO, ocultar el hecho en agregador y eliminar el hecho en dinámica
    if (estadoAnterior == EstadoSolicitud.ACEPTADO || estadoAnterior == EstadoSolicitud.ACEPTADO_MODIFICADO) {
      log.debug("Solicitud estaba aceptada, procesando hecho para solicitud ID: {}", id);
      
      // Buscar el hecho asociado a esta solicitud
      var hechoOpt = hechosRepository.findBySolicitud_Id(id);
      if (hechoOpt.isPresent()) {
        var hecho = hechoOpt.get();
        log.debug("Hecho encontrado para solicitud ID: {}, hecho ID: {}, enviado: {}", id, hecho.getId(), hecho.getEnviado());
        
        // Si el hecho fue enviado al agregador, ocultarlo
        if (Boolean.TRUE.equals(hecho.getEnviado())) {
          // Buscar el hecho en el agregador usando fuente_id=1 e id_origen=hecho.getId()
          try {
            Long idHechoAgregador = buscarIdHechoEnAgregador(1L, hecho.getId());
            if (idHechoAgregador != null) {
              log.debug("Hecho encontrado en agregador con ID: {}, ocultándolo", idHechoAgregador);
              ocultarHechoEnAgregador(idHechoAgregador);
              log.info("Hecho {} ocultado exitosamente en agregador", idHechoAgregador);
            } else {
              log.warn("No se encontró el hecho en agregador para fuente_id=1, id_origen={}", hecho.getId());
            }
          } catch (Exception e) {
            log.error("Error al ocultar hecho en agregador para solicitud ID: {}, hecho ID: {}: {}", 
                id, hecho.getId(), e.getMessage(), e);
            // No lanzar excepción para no bloquear la edición de la solicitud
          }
        }
        
        // Eliminar el hecho en dinámica (esto limpia hecho_id, hecho_enviado)
        hechosRepository.delete(hecho);
        log.debug("Hecho eliminado en dinámica para solicitud ID: {}, hecho ID: {}", id, hecho.getId());
      }
    }

    // Cambiar el estado a PENDIENTE
    solicitud.cambiarEstado(EstadoSolicitud.PENDIENTE);
    log.debug("Estado cambiado a PENDIENTE para solicitud ID: {}", id);

    if (inputDTO.getNuevoTitulo() != null) solicitud.setTitulo(inputDTO.getNuevoTitulo());
    if (inputDTO.getNuevoDescripcion() != null) solicitud.setDescripcion(inputDTO.getNuevoDescripcion());
    if (inputDTO.getNuevoNombreCategoria() != null) solicitud.setCategoria(inputDTO.getNuevoNombreCategoria());
    if (inputDTO.getNuevoLatitud() != null) solicitud.setLatitud(inputDTO.getNuevoLatitud());
    if (inputDTO.getNuevoLongitud() != null) solicitud.setLongitud(inputDTO.getNuevoLongitud());
    if (inputDTO.getNuevoFechaDeAcontecimiento() != null) {
      log.debug("Estableciendo nueva fecha de acontecimiento para solicitud ID: {}", id);
      solicitud.setFechaDeAcontecimiento(inputDTO.getNuevoFechaDeAcontecimiento());
    }

    if (inputDTO.getNuevoContenidoMultimedia() != null) {
      log.debug("Procesando multimedia para solicitud ID: {}, cantidad: {}", id, inputDTO.getNuevoContenidoMultimedia().size());
      solicitud.getMultimedia().clear();
      List<ContenidoMultimedia> nuevaMultimedia = new ArrayList<>();
      for (var dto : inputDTO.getNuevoContenidoMultimedia()) {
        log.debug("Procesando DTO multimedia - path: {}, tipo: {}, tiene base64: {}", 
            dto.getPathArchivo(), dto.getTipoContenido(), 
            dto.getContenidoBase64() != null && !dto.getContenidoBase64().isEmpty());
        
        // Si tiene contenido_base64, guardar el archivo primero
        if (dto.getContenidoBase64() != null && !dto.getContenidoBase64().isEmpty()) {
          log.info("Guardando archivo desde base64 para solicitud ID: {}, tamaño base64: {} caracteres", 
              id, dto.getContenidoBase64().length());
          try {
            String nombreArchivoGuardado = multimediaStorageService.guardarArchivoDesdeBase64(
                dto.getContenidoBase64(),
                dto.getPathArchivo(),
                dto.getTipoContenido() != null ? dto.getTipoContenido().name() : null
            );
            log.info("Archivo guardado exitosamente con nombre: {} para solicitud ID: {}", nombreArchivoGuardado, id);
            dto.setPathArchivo(nombreArchivoGuardado);
          } catch (IOException e) {
            log.error("Error guardando archivo multimedia desde base64 para solicitud ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error guardando archivo multimedia: " + e.getMessage(), e);
          } catch (Exception e) {
            log.error("Error inesperado guardando archivo multimedia desde base64 para solicitud ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error guardando archivo multimedia: " + e.getMessage(), e);
          }
        } else {
          log.debug("DTO multimedia no tiene contenido_base64, usando path existente: {}", dto.getPathArchivo());
        }
        nuevaMultimedia.add(dto.toContenidoMultimedia(solicitud));
      }
      solicitud.getMultimedia().addAll(nuevaMultimedia);
    }

    log.debug("Guardando solicitud modificada ID: {}", id);
    SolicitudHecho solicitudActualizada = solicitudesRepository.save(solicitud);
    log.debug("Solicitud guardada exitosamente ID: {}", id);

    return agregarIdHecho(solicitudActualizada.toOutputDTO(), id);
  }

  @Transactional
  public SolicitudOutputDTO revisionSolicitud(Long id, SolicitudModificarAdminInputDTO inputDTO) {
    log.debug("Revisión administrativa de solicitud ID: {}", id);

    SolicitudHecho solicitud = solicitudesRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + id));

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
    log.debug("Cambiando estado de solicitud ID: {} a {}", id, nuevoEstado);
    solicitud.cambiarEstado(nuevoEstado);

    SolicitudHecho solicitudActualizada = solicitudesRepository.save(solicitud);

    if (solicitud.getEstado() == EstadoSolicitud.ACEPTADO ||
        solicitud.getEstado() == EstadoSolicitud.ACEPTADO_MODIFICADO) {
      log.debug("Creando hecho para solicitud aceptada ID: {}", id);
      fuenteService.crearHechoDesdeSolicitud(solicitudActualizada);
    }

    log.debug("Revisión completada para solicitud ID: {}", id);
    return agregarIdHecho(solicitudActualizada.toOutputDTO(), id);
  }

  @Transactional
  public SolicitudHecho recibirSolicitudContribuyenteConMultipart(
      String solicitudJson,
      List<MultipartFile> archivos) throws IOException {
    
    log.debug("SolicitudesService.recibirSolicitudContribuyenteConMultipart - Iniciando procesamiento");
    log.debug("SolicitudesService.recibirSolicitudContribuyenteConMultipart - JSON recibido: {}", solicitudJson);
    
    SolicitudHechoInputContribuyenteDTO input = parseJsonToDTO(solicitudJson);
    
    log.debug("SolicitudesService.recibirSolicitudContribuyenteConMultipart - DTO parseado - Título: {}, Contribuyente: {}", 
            input.getTitulo(), input.getIdContribuyente());
    
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

  private SolicitudOutputDTO agregarIdHecho(SolicitudOutputDTO dto, Long solicitudId) {
    try {
      var hechoOpt = hechosRepository.findBySolicitud_Id(solicitudId);
      if (hechoOpt.isPresent()) {
        var hecho = hechoOpt.get();
        dto.setIdHecho(hecho.getId());
        dto.setHechoEnviado(hecho.getEnviado());
      }
    } catch (Exception e) {
      log.debug("No se encontró hecho para solicitud ID: {}", solicitudId);
    }
    return dto;
  }

  private Long buscarIdHechoEnAgregador(Long fuenteId, Long idOrigen) {
    try {
      List<HechoAgregadorDTO> hechos = webClientAgregador.get()
          .uri(uriBuilder -> uriBuilder
              .path("/hechos")
              .queryParam("fuente", "Fuente Dinámica")
              .build())
          .retrieve()
          .bodyToFlux(HechoAgregadorDTO.class)
          .collectList()
          .block();

      if (hechos != null && !hechos.isEmpty()) {
        return hechos.stream()
            .filter(h -> h.getFuenteId() != null && h.getFuenteId().equals(fuenteId) &&
                        h.getIdOrigen() != null && h.getIdOrigen().equals(idOrigen))
            .map(HechoAgregadorDTO::getId)
            .findFirst()
            .orElse(null);
      }
      log.debug("No se encontraron hechos en agregador para fuente_id={}, id_origen={}", fuenteId, idOrigen);
      return null;
    } catch (Exception e) {
      log.error("Error al buscar hecho en agregador para fuente_id={}, id_origen={}: {}", 
          fuenteId, idOrigen, e.getMessage(), e);
      return null;
    }
  }

  private void ocultarHechoEnAgregador(Long idHechoAgregador) {
    try {
      webClientAgregador.patch()
          .uri("/hechos/{id}/ocultar", idHechoAgregador)
          .retrieve()
          .bodyToMono(String.class)
          .block();
      log.info("Hecho {} ocultado exitosamente en agregador", idHechoAgregador);
    } catch (WebClientResponseException e) {
      log.error("Error del agregador al ocultar hecho {}: {}", idHechoAgregador, e.getResponseBodyAsString());
      throw new RuntimeException("Error al ocultar hecho en agregador: " + e.getResponseBodyAsString(), e);
    } catch (Exception e) {
      log.error("Error al ocultar hecho {} en agregador: {}", idHechoAgregador, e.getMessage(), e);
      throw new RuntimeException("Error al ocultar hecho en agregador: " + e.getMessage(), e);
    }
  }

  // DTO simple para recibir la respuesta del agregador
  private static class HechoAgregadorDTO {
    @com.fasterxml.jackson.annotation.JsonProperty("id")
    private Long id;
    @com.fasterxml.jackson.annotation.JsonProperty("fuente_id")
    private Long fuenteId;
    @com.fasterxml.jackson.annotation.JsonProperty("id_origen")
    private Long idOrigen;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFuenteId() { return fuenteId; }
    public void setFuenteId(Long fuenteId) { this.fuenteId = fuenteId; }
    public Long getIdOrigen() { return idOrigen; }
    public void setIdOrigen(Long idOrigen) { this.idOrigen = idOrigen; }
  }
}

