package ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.validacion;

import org.springframework.stereotype.Component;

@Component
public class ValidacionLongitud implements Validacion {
    private final long longitudMinima = 500;

    @Override
    public boolean esValida(String texto) {
        return texto != null && texto.trim().length() >= longitudMinima;
    }

    @Override
    public String mensajeError() {
      return "El texto debe tener al menos " + longitudMinima + " caracteres";
    }
}
