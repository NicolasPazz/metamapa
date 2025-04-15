package model.domain.interfaces;

import ar.edu.utn.frba.dds.datasource.dynamic.Hecho;
import model.domain.request.SolicitudDeEliminacion;

public interface Contribuyente {

  public void subir(Hecho hecho);

  public SolicitudDeEliminacion solicitarEliminacion(Hecho hecho, String fundamentos);

}
