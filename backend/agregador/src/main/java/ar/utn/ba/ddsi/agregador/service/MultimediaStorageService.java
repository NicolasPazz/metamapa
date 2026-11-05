package ar.utn.ba.ddsi.agregador.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Obtiene los archivos multimedia desde el servicio dinamica, que los guarda en su base
 * de datos. Asi el agregador no necesita compartir disco con dinamica.
 */
@Service
@Slf4j
public class MultimediaStorageService {

    private final String dinamicaUrl;
    private final HttpClient httpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();

    public MultimediaStorageService(@Value("${DINAMICA_URL}") String dinamicaUrl) {
        this.dinamicaUrl = dinamicaUrl.endsWith("/") ? dinamicaUrl.substring(0, dinamicaUrl.length() - 1) : dinamicaUrl;
    }

    public Resource cargarArchivo(String nombreArchivo) throws IOException {
        validarNombreArchivo(nombreArchivo);

        HttpRequest.Builder request = HttpRequest.newBuilder()
            .uri(URI.create(dinamicaUrl + "/api/multimedia/" + nombreArchivo))
            // dinamica puede tardar en responder si estaba suspendida (ej. plan gratuito de Render)
            .timeout(Duration.ofSeconds(120))
            .GET();
        String internalToken = System.getenv().getOrDefault("INTERNAL_API_TOKEN", "");
        if (!internalToken.isBlank()) {
            request.header("X-Internal-Token", internalToken);
        }

        HttpResponse<byte[]> response;
        try {
            response = httpClient.send(request.build(), HttpResponse.BodyHandlers.ofByteArray());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrumpido obteniendo el archivo: " + nombreArchivo, e);
        }

        if (response.statusCode() != 200) {
            log.warn("No se pudo obtener el archivo {} desde dinamica: HTTP {}", nombreArchivo, response.statusCode());
            throw new IOException("No se pudo obtener el archivo: " + nombreArchivo);
        }

        return new ByteArrayResource(response.body()) {
            @Override
            public String getFilename() {
                return nombreArchivo;
            }
        };
    }

    private void validarNombreArchivo(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede estar vacío");
        }

        if (nombreArchivo.contains("..") || nombreArchivo.contains("/") || nombreArchivo.contains("\\")) {
            throw new SecurityException("Nombre de archivo inválido: contiene caracteres no permitidos");
        }

        if (!nombreArchivo.matches("^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}\\.[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("Nombre de archivo inválido: formato esperado UUID.extension");
        }
    }
}
