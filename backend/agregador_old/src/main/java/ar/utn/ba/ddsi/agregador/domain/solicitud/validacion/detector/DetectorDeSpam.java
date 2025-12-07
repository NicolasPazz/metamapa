package ar.utn.ba.ddsi.agregador.domain.solicitud.validacion.detector;

public interface DetectorDeSpam {
  boolean esSpam(String texto);
}