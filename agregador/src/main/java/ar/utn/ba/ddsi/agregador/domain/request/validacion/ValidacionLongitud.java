package ar.utn.ba.ddsi.agregador.domain.request.validacion;

import ar.utn.ba.ddsi.agregador.domain.request.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.agregador.domain.request.validacion.Validacion;

public class ValidacionLongitud implements Validacion {
  private int longitudMinima = 500;

  @Override
  public Boolean cumple(SolicitudDeEliminacion solicitud) {
    String textoFundamentos = solicitud.getTextoFundamentos();
    return textoFundamentos.length() >= longitudMinima;
  }
}
