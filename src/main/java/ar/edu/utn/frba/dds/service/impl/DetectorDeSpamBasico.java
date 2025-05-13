package ar.edu.utn.frba.dds.service.impl;

import ar.edu.utn.frba.dds.service.DetectorDeSpam;
import org.springframework.stereotype.Component;

@Component
public class DetectorDeSpamBasico implements DetectorDeSpam {
  @Override
  public boolean esSpam(String texto) {
    return texto.length() < 500;
  }
}
