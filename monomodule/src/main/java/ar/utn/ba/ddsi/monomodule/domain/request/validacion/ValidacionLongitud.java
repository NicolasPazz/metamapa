package ar.utn.ba.ddsi.monomodule.domain.request.validacion;

import ar.utn.ba.ddsi.monomodule.domain.request.SolicitudDeEliminacion;

public class ValidacionLongitud implements Validacion {
  private int longitudMinima = 500;

  @Override
  public Boolean cumple(SolicitudDeEliminacion solicitud) {
    String textoFundamentos = solicitud.getTextoFundamentos();
    return textoFundamentos.length() >= longitudMinima;
  }
}
