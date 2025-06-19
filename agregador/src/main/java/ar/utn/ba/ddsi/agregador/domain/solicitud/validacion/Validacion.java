package ar.utn.ba.ddsi.agregador.domain.solicitud.validacion;

import ar.utn.ba.ddsi.agregador.domain.solicitud.SolicitudDeEliminacion;

public interface Validacion {
    /**
     * Valida si la solicitud es válida.
     *
     * @param solicitud la solicitud a validar
     * @return true si la solicitud es válida, false en caso contrario
     */
    boolean esValida(String texto);
}
