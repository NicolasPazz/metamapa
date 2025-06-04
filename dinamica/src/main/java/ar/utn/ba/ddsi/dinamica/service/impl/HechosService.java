package ar.utn.ba.ddsi.dinamica.service.impl;

import ar.utn.ba.ddsi.dinamica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.hecho.*;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;

import ar.utn.ba.ddsi.dinamica.service.IHechosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class HechosService implements IHechosService {

  @Autowired
  private IHechosRepository hechosRepository; //los hechos los voy a sacar de un repositorio

  @Override
  public List<HechoOutputDTO> buscarTodos() {
    // TODO: asegurarme de que el usuario que haga esta peticion tenga permisos(tener en cuenta para modificar un hecho con users logueados)
    List<Hecho> hechos = this.hechosRepository.findAll();
    List<HechoOutputDTO> dtoList = hechos
            .stream()
            .map(this::hechoOutputDTO)
            .toList();
    return dtoList;
  }


  private HechoOutputDTO hechoOutputDTO(Hecho hecho){
    var dto = new HechoOutputDTO();
    dto.setId(hecho.getId());
    dto.setTitulo(hecho.getTitulo());
    dto.setDescripcion(hecho.getDescripcion());
    dto.setCategoria(hecho.getNombreCategoria());
    dto.setLatitud(hecho.getLatitud());
    dto.setLongitud(hecho.getLongitud());
    dto.setFechaDeAcontecimiento(hecho.getFechaDeAcontecimiento().toString());
    dto.setFechaDeCarga(hecho.getFechaDeCarga().toString());
    dto.setOrigen(hecho.getOrigen().name().toLowerCase());
    dto.setIdContribuyente(hecho.getIdContribuyente());
    dto.setMultimediaPaths(hecho.getMultimediaPaths());
    return dto;
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
    hecho.setIdSolicitud(solicitud.getIdSolicitud());
    hecho.setData(dataHecho);
    hecho.setOrigen(OrigenHecho.CONTRIBUYENTE);
    hecho.setIdContribuyente(solicitud.getIdContribuyente());
    hecho.setMultimediaPaths(solicitud.getMultimedia());

    Hecho busqueda = this.hechosRepository.findByIdSolicitud(solicitud.getIdSolicitud());
    if (busqueda == null) {
      this.hechosRepository.save(hecho);
    }else{
      this.hechosRepository.update(hecho);
    }
  }

}
