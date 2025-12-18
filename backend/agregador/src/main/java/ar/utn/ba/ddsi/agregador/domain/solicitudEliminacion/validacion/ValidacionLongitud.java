package ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.validacion;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidacionLongitud implements Validacion {
    private final long longitudMinima = 500;

    @Override
    public boolean esValida(String texto) {
        int longitud = texto != null ? texto.trim().length() : 0;
        boolean resultado = texto != null && longitud >= longitudMinima;

        log.info("[ValidacionLongitud] esValida() llamado");
        log.info("[ValidacionLongitud] Longitud del texto: {} caracteres", longitud);
        log.info("[ValidacionLongitud] Longitud mínima requerida: {} caracteres", longitudMinima);
        log.info("[ValidacionLongitud] Resultado: {}", resultado ? "VÁLIDO" : "INVÁLIDO");

        return resultado;
    }

    @Override
    public String mensajeError() {
      return "El texto de fundamentos debe tener al menos " + longitudMinima + " caracteres";
    }
}
