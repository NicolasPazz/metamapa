package ar.utn.ba.ddsi.dinamica.service;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.ContenidoMultimediaInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudHechoInputContribuyenteDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarAdminInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.input.SolicitudModificarUserInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.HechoAgregadorDTO;
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
    List<SolicitudHecho> solicitudes = solicitudesRepository.findAll();
    return solicitudes.stream()
        .map(s -> agregarIdHecho(s.toOutputDTO(), s.getId()))
        .toList();
  }

  public List<SolicitudOutputDTO> buscarPendientes() {
    List<SolicitudHecho> solicitudes = solicitudesRepository.findAll();
    return solicitudes.stream()
        .filter(s -> s.getEstado() == EstadoSolicitud.PENDIENTE)
        .map(s -> agregarIdHecho(s.toOutputDTO(), s.getId()))
        .toList();
  }

  public List<SolicitudOutputDTO> buscarPorContribuyente(Long idContribuyente) {
    List<SolicitudHecho> solicitudes = solicitudesRepository.findByIdContribuyente(idContribuyente);
    return solicitudes.stream()
        .map(s -> agregarIdHecho(s.toOutputDTO(), s.getId()))
        .toList();
  }

  @Transactional
  public SolicitudHecho recibirSolicitudContribuyente(SolicitudHechoInputContribuyenteDTO input) {
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

    solicitud.validarCoordenadas();
    solicitud.validarFechaNoFutura();

    if (input.getMultimedia() != null && !input.getMultimedia().isEmpty()) {
      if (input.getMultimedia().size() > 5) {
        throw new IllegalArgumentException("Solo se pueden cargar hasta 5 archivos. Se recibieron " + input.getMultimedia().size() + " archivos.");
      }
      
      List<ContenidoMultimedia> multimedia = input.getMultimedia().stream()
          .map(dto -> dto.toContenidoMultimedia(solicitud))
          .toList();
      solicitud.getMultimedia().addAll(multimedia);
    }

    solicitud.cambiarEstado(EstadoSolicitud.PENDIENTE);
    return solicitudesRepository.save(solicitud);
  }

  public SolicitudOutputDTO obtenerSolicitud(Long id) {
    SolicitudHecho solicitud = solicitudesRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + id));

    return agregarIdHecho(solicitud.toOutputDTO(), id);
  }

  @Transactional
  public SolicitudOutputDTO modificarSolicitudConMultipart(
      Long id, String solicitudJson, List<MultipartFile> archivos) throws IOException {
    SolicitudModificarUserInputDTO inputDTO = parseJsonToModificarDTO(solicitudJson);
    
    if (archivos != null && !archivos.isEmpty()) {
      long archivosNoVacios = archivos.stream().filter(archivo -> !archivo.isEmpty()).count();
      if (archivosNoVacios > 5) {
        throw new IllegalArgumentException("Solo se pueden cargar hasta 5 archivos. Se recibieron " + archivosNoVacios + " archivos.");
      }
      
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
    SolicitudHecho solicitud = solicitudesRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + id));

    solicitud.validarModificacionPorUsuario(inputDTO.getIdContribuyente());

    EstadoSolicitud estadoAnterior = solicitud.getEstado();

    if (estadoAnterior == EstadoSolicitud.ACEPTADO || estadoAnterior == EstadoSolicitud.ACEPTADO_MODIFICADO) {
      var hechoOpt = hechosRepository.findBySolicitud_Id(id);
      if (hechoOpt.isPresent()) {
        var hecho = hechoOpt.get();
        if (Boolean.TRUE.equals(hecho.getEnviado())) {
          try {
            Long idHechoAgregador = buscarIdHechoEnAgregador(1L, hecho.getId());
            if (idHechoAgregador != null) {
              ocultarHechoEnAgregador(idHechoAgregador);
            }
          } catch (Exception e) {
            log.error("Error al ocultar hecho en agregador para solicitud ID: {}, hecho ID: {}: {}", 
                id, hecho.getId(), e.getMessage(), e);
          }
        }
        hechosRepository.delete(hecho);
      }
    }

    solicitud.cambiarEstado(EstadoSolicitud.PENDIENTE);

    if (inputDTO.getNuevoTitulo() != null) solicitud.setTitulo(inputDTO.getNuevoTitulo());
    if (inputDTO.getNuevoDescripcion() != null) solicitud.setDescripcion(inputDTO.getNuevoDescripcion());
    if (inputDTO.getNuevoNombreCategoria() != null) solicitud.setCategoria(inputDTO.getNuevoNombreCategoria());
    if (inputDTO.getNuevoLatitud() != null) solicitud.setLatitud(inputDTO.getNuevoLatitud());
    if (inputDTO.getNuevoLongitud() != null) solicitud.setLongitud(inputDTO.getNuevoLongitud());
    if (inputDTO.getNuevoFechaDeAcontecimiento() != null) {
      solicitud.setFechaDeAcontecimiento(inputDTO.getNuevoFechaDeAcontecimiento());
    }

    if (inputDTO.getNuevoContenidoMultimedia() != null) {
      if (inputDTO.getNuevoContenidoMultimedia().size() > 5) {
        throw new IllegalArgumentException("Solo se pueden cargar hasta 5 archivos. Se recibieron " + inputDTO.getNuevoContenidoMultimedia().size() + " archivos.");
      }
      
      solicitud.getMultimedia().clear();
      List<ContenidoMultimedia> nuevaMultimedia = new ArrayList<>();
      for (var dto : inputDTO.getNuevoContenidoMultimedia()) {
        if (dto.getContenidoBase64() != null && !dto.getContenidoBase64().isEmpty()) {
          try {
            String nombreArchivoGuardado = multimediaStorageService.guardarArchivoDesdeBase64(
                dto.getContenidoBase64(),
                dto.getPathArchivo(),
                dto.getTipoContenido() != null ? dto.getTipoContenido().name() : null
            );
            dto.setPathArchivo(nombreArchivoGuardado);
          } catch (IOException e) {
            log.error("Error guardando archivo multimedia desde base64 para solicitud ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error guardando archivo multimedia: " + e.getMessage(), e);
          } catch (Exception e) {
            log.error("Error inesperado guardando archivo multimedia desde base64 para solicitud ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Error guardando archivo multimedia: " + e.getMessage(), e);
          }
        }
        nuevaMultimedia.add(dto.toContenidoMultimedia(solicitud));
      }
      solicitud.getMultimedia().addAll(nuevaMultimedia);
    }

    LocalDateTime fechaDeCargaOriginal = solicitud.getFechaDeCargaSolicitud();
    SolicitudHecho solicitudActualizada = solicitudesRepository.save(solicitud);
    
    if (solicitudActualizada.getFechaDeCargaSolicitud() != null && 
        fechaDeCargaOriginal != null &&
        !solicitudActualizada.getFechaDeCargaSolicitud().equals(fechaDeCargaOriginal)) {
      log.warn("ADVERTENCIA: fechaDeCargaSolicitud fue modificada durante save. Restaurando valor original.");
      solicitudActualizada.setFechaDeCargaSolicitud(fechaDeCargaOriginal);
      solicitudActualizada = solicitudesRepository.save(solicitudActualizada);
    }

    return agregarIdHecho(solicitudActualizada.toOutputDTO(), id);
  }

  @Transactional
  public SolicitudOutputDTO revisionSolicitud(Long id, SolicitudModificarAdminInputDTO inputDTO) {
    SolicitudHecho solicitud = solicitudesRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada con ID: " + id));
    
    LocalDateTime fechaDeCargaOriginal = solicitud.getFechaDeCargaSolicitud();

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
    solicitud.cambiarEstado(nuevoEstado);
    SolicitudHecho solicitudActualizada = solicitudesRepository.save(solicitud);
    
    if (solicitudActualizada.getFechaDeCargaSolicitud() != null && 
        fechaDeCargaOriginal != null &&
        !solicitudActualizada.getFechaDeCargaSolicitud().equals(fechaDeCargaOriginal)) {
      log.warn("ADVERTENCIA: fechaDeCargaSolicitud fue modificada durante save en revisión. Restaurando valor original.");
      solicitudActualizada.setFechaDeCargaSolicitud(fechaDeCargaOriginal);
      solicitudActualizada = solicitudesRepository.save(solicitudActualizada);
    }

    if (solicitud.getEstado() == EstadoSolicitud.ACEPTADO ||
        solicitud.getEstado() == EstadoSolicitud.ACEPTADO_MODIFICADO) {
      fuenteService.crearHechoDesdeSolicitud(solicitudActualizada);
    }
    return agregarIdHecho(solicitudActualizada.toOutputDTO(), id);
  }

  @Transactional
  public SolicitudHecho recibirSolicitudContribuyenteConMultipart(
      String solicitudJson,
      List<MultipartFile> archivos) throws IOException {
    SolicitudHechoInputContribuyenteDTO input = parseJsonToDTO(solicitudJson);
    
    if (archivos != null && !archivos.isEmpty()) {
      long archivosNoVacios = archivos.stream().filter(archivo -> !archivo.isEmpty()).count();
      if (archivosNoVacios > 5) {
        throw new IllegalArgumentException("Solo se pueden cargar hasta 5 archivos. Se recibieron " + archivosNoVacios + " archivos.");
      }
      
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
      log.debug("No se pudo obtener el hecho asociado a la solicitud {}: {}", solicitudId, e.getMessage());
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

}

