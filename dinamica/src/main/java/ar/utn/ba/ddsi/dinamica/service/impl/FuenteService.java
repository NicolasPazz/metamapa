package ar.utn.ba.ddsi.dinamica.service.impl;

import ar.utn.ba.ddsi.dinamica.domain.dto.input.ContenidoMultimediaInputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.ContenidoMultimediaOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.hecho.*;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;

import ar.utn.ba.ddsi.dinamica.service.IFuenteService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Service
public class FuenteService implements IFuenteService {
  private final IHechosRepository hechosRepository; //los hechos los voy a sacar de un repositorio

  public FuenteService(IHechosRepository hechosRepository) {
    this.hechosRepository = hechosRepository;
  }

  @Override
  public List<HechoOutputDTO> getAllHechosVisibles(String categoria, String fecha_reporte_desde, String fecha_reporte_hasta, String fecha_acontecimiento_desde, String fecha_acontecimiento_hasta, String ubicacion) {
    return List.of();
  }

  // Para enviar solo los hechos nuevos, ya sea por creacion, actualizacion o cualquier transicion de estado
  private LocalDateTime ultimaLlamada = null;

  @Override
  public List<HechoOutputDTO> getAllHechosNuevos() {
    LocalDateTime ahora = LocalDateTime.now();
    List<Hecho> hechos = this.hechosRepository.findAll();
    List<HechoOutputDTO> nuevos = hechos.stream()
        .filter(h ->ultimaLlamada == null || h.getUltimaActualizacion().isAfter(ultimaLlamada))
        .map(this::hechoOutputDTO)
        .toList();
    // Actualizar la ultima llamada
    ultimaLlamada = ahora;

    return nuevos;
  }

  @Override
  public void crearHechoDesdeSolicitud(SolicitudHecho solicitud) {
    DataHecho dataHecho = new DataHecho();
    dataHecho.setTitulo(solicitud.getTitulo());
    dataHecho.setDescripcion(solicitud.getDescripcion());
    dataHecho.setNombreCategoria(solicitud.getCategoria());
    dataHecho.setLatitud(solicitud.getLatitud());
    dataHecho.setLongitud(solicitud.getLongitud());
    dataHecho.setFechaDeAcontecimiento(solicitud.getFechaDeAcontecimiento());
    List<ContenidoMultimedia> multimediaConvertida = new ArrayList<>();
    if (solicitud.getMultimedia() != null) {
      for (ContenidoMultimediaInputDTO dto : solicitud.getMultimedia()) {
        try {
          TipoContenido tipo = TipoContenido.valueOf(dto.getTipoContenido().toUpperCase());
          multimediaConvertida.add(new ContenidoMultimedia(dto.getPathArchivo(), tipo));
        } catch (IllegalArgumentException e) {
          throw new ResponseStatusException(
                  HttpStatus.BAD_REQUEST,
                  "Tipo de contenido inválido en solicitud: " + dto.getTipoContenido()
          );
        }
      }
    }

    Hecho hecho = new Hecho();
    hecho.setIdSolicitud(solicitud.getIdSolicitud());
    hecho.setData(dataHecho);
    hecho.setOrigen(OrigenHecho.CONTRIBUYENTE);
    hecho.setIdContribuyente(solicitud.getIdContribuyente());
    hecho.setMultimediaPaths(multimediaConvertida);

    Hecho busqueda = this.hechosRepository.findByIdSolicitud(solicitud.getIdSolicitud());
    if (busqueda == null) {
      this.hechosRepository.save(hecho);
    }else{
      hecho.setId(busqueda.getId());
      this.hechosRepository.update(hecho);
    }
  }

  private HechoOutputDTO hechoOutputDTO(Hecho hecho){

    List<ContenidoMultimediaOutputDTO> multimedia = hecho.getMultimediaPaths().stream()
            .map(m -> new ContenidoMultimediaOutputDTO(
                    m.getPathArchivo(),
                    m.getTipoContenido().name()
            ))
            .toList();

    return new HechoOutputDTO(
            hecho.getId(),
            hecho.getTitulo(),
            hecho.getDescripcion(),
            hecho.getNombreCategoria(),
            hecho.getLatitud(),
            hecho.getLongitud(),
            hecho.getFechaDeAcontecimiento(),
            hecho.getFechaDeCarga(),
            hecho.getUltimaActualizacion(),
            hecho.getIdContribuyente(),
            multimedia
           );
  }
}
