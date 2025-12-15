package ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.validacion.detector;

public interface DetectorDeSpam {
  boolean esSpam(String texto);
}