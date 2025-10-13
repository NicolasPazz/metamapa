package ar.utn.ba.ddsi.estadistica.service;

public interface IDetectorSpam {
    // Verifica si el texto es spam
    boolean esSpam(String texto);
}
