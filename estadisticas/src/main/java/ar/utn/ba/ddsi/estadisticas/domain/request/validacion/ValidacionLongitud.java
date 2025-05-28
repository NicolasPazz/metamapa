package ar.utn.ba.ddsi.estadisticas.domain.request.validacion;

import ar.utn.ba.ddsi.estadisticas.domain.request.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.estadisticas.domain.request.validacion.Validacion;

public class ValidacionLongitud implements Validacion {
  private int longitudMinima = 500;

  @Override
  public Boolean cumple(SolicitudDeEliminacion solicitud) {
    String textoFundamentos = solicitud.getTextoFundamentos();
    return textoFundamentos.length() >= longitudMinima;
  }
}
