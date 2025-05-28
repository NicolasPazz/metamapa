package ar.utn.ba.ddsi.estadisticas.service;

public interface DetectorDeSpam {
  boolean esSpam(String texto);
}