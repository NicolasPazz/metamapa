package ar.utn.ba.ddsi.agregador.domain.solicitud.detector;

public interface DetectorDeSpam {
  boolean esSpam(String texto);
}