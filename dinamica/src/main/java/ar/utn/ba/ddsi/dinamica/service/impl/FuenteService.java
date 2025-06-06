package ar.utn.ba.ddsi.dinamica.service.impl;

import ar.utn.ba.ddsi.dinamica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.hecho.*;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;

import ar.utn.ba.ddsi.dinamica.service.IFuenteService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
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


    Hecho hecho = new Hecho();
    hecho.setIdSolicitud(solicitud.getIdSolicitud());
    hecho.setData(dataHecho);
    hecho.setOrigen(OrigenHecho.CONTRIBUYENTE);
    hecho.setIdContribuyente(solicitud.getIdContribuyente());
    hecho.setMultimediaPaths(solicitud.getMultimedia());

    Hecho busqueda = this.hechosRepository.findByIdSolicitud(solicitud.getIdSolicitud());
    if (busqueda == null) {
      this.hechosRepository.save(hecho);
    }else{
      hecho.setId(busqueda.getId());
      this.hechosRepository.update(hecho);
    }
  }

  private HechoOutputDTO hechoOutputDTO(Hecho hecho){
    var dto = new HechoOutputDTO();
    dto.setId(hecho.getId());
    dto.setOrigen(hecho.getOrigen().name());
    //dto.setEstado(hecho.getEstado().name());
    dto.setIdContribuyente(hecho.getIdContribuyente());
    dto.setIdEtiquetas(hecho.getEtiquetas().stream()
        .map(Etiqueta::getId)
        .toList());
    dto.setTitulo(hecho.getTitulo());
    dto.setDescripcion(hecho.getDescripcion());
    dto.setCategoria(hecho.getNombreCategoria());
    dto.setLatitud(hecho.getLatitud());
    dto.setLongitud(hecho.getLongitud());
    dto.setFechaDeAcontecimiento(hecho.getFechaDeAcontecimiento().toString());
    dto.setFechaDeCarga(hecho.getFechaDeCarga().toString());
    dto.setMultimediaPaths(hecho.getMultimediaPaths());
    return dto;
  }
}
