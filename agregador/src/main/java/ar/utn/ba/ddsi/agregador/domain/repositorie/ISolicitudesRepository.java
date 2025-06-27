package ar.utn.ba.ddsi.agregador.domain.repositorie;

import ar.utn.ba.ddsi.agregador.domain.solicitud.SolicitudDeEliminacion;

import java.util.List;

public interface ISolicitudesRepository {
  void save(SolicitudDeEliminacion solicitudDeEliminacion);
  public List<SolicitudDeEliminacion> findAll();
  SolicitudDeEliminacion findById(Long idSolicitud);
  public void eliminar(Long id);
  }
