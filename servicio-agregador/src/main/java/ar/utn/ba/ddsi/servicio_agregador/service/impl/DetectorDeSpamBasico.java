package ar.utn.ba.ddsi.servicio_agregador.service.impl;

import ar.utn.ba.ddsi.servicio_agregador.service.DetectorDeSpam;
import org.springframework.stereotype.Component;

// FIXME: Si no es service no deberia ir en la capa de service
@Component
public class DetectorDeSpamBasico implements DetectorDeSpam {
  @Override
  public boolean esSpam(String texto) {
    return texto.length() < 500;
  }
}
