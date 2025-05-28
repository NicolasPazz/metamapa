package ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.request.validacion;

import ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.request.SolicitudDeEliminacion;

public class ValidacionLongitud implements Validacion {
  private int longitudMinima = 500;

  @Override
  public Boolean cumple(SolicitudDeEliminacion solicitud) {
    String textoFundamentos = solicitud.getTextoFundamentos();
    return textoFundamentos.length() >= longitudMinima;
  }
}
