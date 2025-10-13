package ar.utn.ba.ddsi.estadistica.service;

import ar.utn.ba.ddsi.estadistica.domain.SolicitudEliminacion;
import java.util.List;

public interface ISolicitudService {
  List<SolicitudEliminacion> refrescarSolicitudes();
}
