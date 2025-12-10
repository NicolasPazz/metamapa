package ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.validacion;

import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.SolicitudDeEliminacion;

public class ValidacionLongitud implements Validacion {
    private final long longitudMinima;

    public ValidacionLongitud(long longitudMinima) {
        this.longitudMinima = longitudMinima;
    }

    @Override
    public boolean esValida(String texto) {
        return texto != null && texto.length() >= longitudMinima;
    }
}
