package ar.utn.ba.ddsi.frontend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        log.warn("Intento de subir archivo que excede el tamaño máximo permitido: {}", ex.getMessage());
        
        String mensaje = "El tamaño del archivo excede el límite permitido de 50 MB. " +
                        "Por favor, comprima o reduzca el tamaño de los archivos multimedia e intente nuevamente.";
        
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(mensaje);
    }
}
