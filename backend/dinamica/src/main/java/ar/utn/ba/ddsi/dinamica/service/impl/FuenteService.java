package ar.utn.ba.ddsi.dinamica.service.impl;

import ar.utn.ba.ddsi.dinamica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.hecho.ContenidoMultimedia;
import ar.utn.ba.ddsi.dinamica.domain.hecho.EstadoHecho;
import ar.utn.ba.ddsi.dinamica.domain.hecho.Hecho;
import ar.utn.ba.ddsi.dinamica.domain.hecho.OrigenHecho;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.ISolicitudesRepository;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;
import ar.utn.ba.ddsi.dinamica.service.IFuenteService;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FuenteService implements IFuenteService {
  private static final Logger LOGGER = LoggerFactory.getLogger(FuenteService.class);
  private final IHechosRepository hechosRepository;
  private final ISolicitudesRepository solicitudesRepository;

  public FuenteService(IHechosRepository hechosRepository, ISolicitudesRepository solicitudesRepository) {
    this.hechosRepository = hechosRepository;
    this.solicitudesRepository = solicitudesRepository;
  }

  @Override
  public List<HechoOutputDTO> getAllHechosVisibles(String categoria, LocalDate fecha_reporte_desde,
                                                   LocalDate fecha_reporte_hasta, LocalDate fecha_acontecimiento_desde,
                                                   LocalDate fecha_acontecimiento_hasta, String ubicacion) {
    return List.of();
  }

  @Override
  public List<HechoOutputDTO> getAllHechosNuevos() {

    List<Hecho> noEnviados = hechosRepository.findByEnviadoFalse();

    // Para cada hecho: buscar la solicitud asociada y tomar su multimedia
    for (Hecho h : noEnviados) {
      try {
        if (h.getSolicitud() != null && h.getSolicitud().getId() != null) {
          Long idSolicitud = h.getSolicitud().getId();
          Optional<SolicitudHecho> optionalSolicitud = solicitudesRepository.findById(idSolicitud);
          if (optionalSolicitud.isPresent()) {
            SolicitudHecho solicitud = optionalSolicitud.get();
            // asignar multimedia de la solicitud al hecho (si es null -> lista vacía)
            List<ContenidoMultimedia> multimedia = solicitud.getMultimedia();
            h.setMultimedia(multimedia != null ? new ArrayList<>(multimedia) : new ArrayList<>());
          } else {
            LOGGER.warn("Solicitud con id {} no encontrada para el hecho id {}", idSolicitud, h.getId());
            h.setMultimedia(new ArrayList<>());
          }
        } else {
          // no hay solicitud asociada
          h.setMultimedia(new ArrayList<>());
          LOGGER.warn("Solicitud no encontrada para el hecho id {}", h.getId());
        }
      } catch (Exception ex) {
        LOGGER.error("Error obteniendo multimedia desde solicitud para hecho id {}: {}", h.getId(), ex.getMessage(), ex);
        h.setMultimedia(new ArrayList<>());
      }
      h.setEnviado(Boolean.TRUE);
    }

    hechosRepository.saveAll(noEnviados);

    return noEnviados.stream()
        .map(Hecho::toOutputDTO)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void crearHechoDesdeSolicitud(SolicitudHecho solicitud) {
    LOGGER.info("Creando hecho desde solicitud: {}", solicitud);
    if (solicitud == null) {
      LOGGER.error("Solicitud nula al crear hecho");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solicitud vacía");
    }

    Hecho hecho = new Hecho();
    hecho.setTitulo(solicitud.getTitulo());
    hecho.setDescripcion(solicitud.getDescripcion());
    hecho.setNombreCategoria(solicitud.getCategoria());
    hecho.setLatitud(solicitud.getLatitud());
    hecho.setLongitud(solicitud.getLongitud());
    hecho.setFechaDeAcontecimiento(solicitud.getFechaDeAcontecimiento());
    hecho.setOrigen(OrigenHecho.CONTRIBUYENTE);
    hecho.setIdContribuyente(solicitud.getIdContribuyente());
    hecho.setEnviado(Boolean.FALSE);
    hecho.setSolicitud(solicitud);
    hecho.setEstado(EstadoHecho.VISIBLE);

    // CLONAR multimedia: crear nuevas entidades para el Hecho (NO reutilizar instancias de la Solicitud)
    List<ContenidoMultimedia> multimediaSolicitud = solicitud.getMultimedia();
    if (multimediaSolicitud != null && !multimediaSolicitud.isEmpty()) {
      LOGGER.debug("Solicitud contiene multimedia, clonando para el hecho.");
      List<ContenidoMultimedia> multimediaParaHecho = new ArrayList<>();
      for (ContenidoMultimedia cmSolicitud : multimediaSolicitud) {
        ContenidoMultimedia cm = new ContenidoMultimedia();
        cm.setPathArchivo(cmSolicitud.getPathArchivo());
        cm.setTipoContenido(cmSolicitud.getTipoContenido());
        multimediaParaHecho.add(cm);
      }
      hecho.setMultimedia(multimediaParaHecho);
    } else {
      hecho.setMultimedia(new ArrayList<>());
    }

    try {
      hechosRepository.save(hecho);
      LOGGER.info("Hecho creado y guardado con ID: {}", hecho.getId());
    } catch (Exception ex) {
      LOGGER.error("Error al crear Hecho desde Solicitud id={}: {}", solicitud.getId(), ex.getMessage(), ex);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al crear hecho desde solicitud: "
          + ex.getMessage(), ex);
    }
    LOGGER.info("Hecho creado y guardado con ID: {}", hecho.getId());
  }
}
