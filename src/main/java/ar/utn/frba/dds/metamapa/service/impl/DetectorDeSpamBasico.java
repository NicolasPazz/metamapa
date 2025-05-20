package ar.utn.frba.dds.metamapa.service.impl;

import ar.utn.frba.dds.metamapa.service.DetectorDeSpam;
import org.springframework.stereotype.Component;

// FIXME: Si no es service no deberia ir en la capa de service
@Component
public class DetectorDeSpamBasico implements DetectorDeSpam {
  @Override
  public boolean esSpam(String texto) {
    return texto.length() < 500;
  }
}
