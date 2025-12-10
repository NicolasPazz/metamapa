package ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.validacion;

public interface Validacion {
    boolean esValida(String texto);
    String mensajeError();
}
