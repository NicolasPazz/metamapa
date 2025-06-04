package ar.utn.ba.ddsi.dinamica.service;

import ar.utn.ba.ddsi.dinamica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;

import java.util.List;

public interface IHechosService {
  public List<HechoOutputDTO> buscarTodos();
  public void crearHechoDesdeSolicitud(SolicitudHecho solicitud);

}
