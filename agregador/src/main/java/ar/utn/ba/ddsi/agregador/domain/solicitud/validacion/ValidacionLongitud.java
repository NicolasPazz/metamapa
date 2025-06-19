package ar.utn.ba.ddsi.agregador.domain.solicitud.validacion;

import ar.utn.ba.ddsi.agregador.domain.solicitud.SolicitudDeEliminacion;

public class ValidacionLongitud implements Validacion {
    private static final int LONGITUD_MINIMA = 500;

    @Override
    public boolean esValida(String texto) {
        return texto != null && texto.length() >= LONGITUD_MINIMA;
    }
}
