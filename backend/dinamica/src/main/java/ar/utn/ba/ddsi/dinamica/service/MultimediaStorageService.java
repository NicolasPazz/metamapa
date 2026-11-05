package ar.utn.ba.ddsi.dinamica.service;

import ar.utn.ba.ddsi.dinamica.domain.multimedia.ArchivoMultimedia;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.IArchivoMultimediaRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Guarda los archivos multimedia en la base de datos. Asi no dependen del disco del
 * servidor y el agregador los puede pedir por HTTP (GET /api/multimedia/{nombre}).
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MultimediaStorageService {

    private final IArchivoMultimediaRepository archivoRepository;

    /** Tamano maximo por archivo, en MB. */
    @Value("${multimedia.max-file-size-mb:50}")
    private long maxFileSizeMb;

    private static final String[] FORMATOS_IMAGEN = {"jpg", "jpeg", "png", "gif", "webp"};
    private static final String[] FORMATOS_VIDEO = {"mp4", "webm"};
    private static final String[] FORMATOS_AUDIO = {"mp3", "wav", "ogg"};

    @Transactional
    public String guardarArchivo(MultipartFile archivo) throws IOException {
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        String nombreOriginal = archivo.getOriginalFilename();
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            throw new IllegalArgumentException("El archivo debe tener una extensión válida");
        }

        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".") + 1).toLowerCase();
        validarFormato(extension);
        validarTamanio(archivo.getSize());

        return guardar(extension, archivo.getBytes());
    }

    @Transactional
    public String guardarArchivoDesdeBase64(String contenidoBase64, String nombreOriginal, String tipoContenido) throws IOException {
        if (contenidoBase64 == null || contenidoBase64.isEmpty()) {
            throw new IllegalArgumentException("El contenido base64 está vacío");
        }

        String extension = null;
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".") + 1).toLowerCase();
        } else if (tipoContenido != null) {
            extension = switch (tipoContenido.toUpperCase()) {
                case "IMAGEN" -> "jpg";
                case "VIDEO" -> "mp4";
                case "AUDIO" -> "mp3";
                default -> null;
            };
        }
        validarFormato(extension);

        byte[] bytes = Base64.getDecoder().decode(contenidoBase64);
        validarTamanio(bytes.length);

        return guardar(extension, bytes);
    }

    @Transactional(readOnly = true)
    public Resource cargarArchivo(String nombreArchivo) throws IOException {
        validarNombreArchivo(nombreArchivo);

        ArchivoMultimedia archivo = archivoRepository.findById(nombreArchivo)
            .orElseThrow(() -> new IOException("No se encontró el archivo: " + nombreArchivo));

        return new ByteArrayResource(archivo.getContenido()) {
            @Override
            public String getFilename() {
                return nombreArchivo;
            }
        };
    }

    private String guardar(String extension, byte[] contenido) {
        String nombreArchivo = UUID.randomUUID() + "." + extension;
        archivoRepository.save(ArchivoMultimedia.builder()
            .nombre(nombreArchivo)
            .tamanio((long) contenido.length)
            .fechaSubida(LocalDateTime.now())
            .contenido(contenido)
            .build());
        log.info("Archivo multimedia guardado: {} ({} bytes)", nombreArchivo, contenido.length);
        return nombreArchivo;
    }

    private void validarFormato(String extension) {
        if (extension == null || !esFormatoPermitido(extension)) {
            throw new IllegalArgumentException(
                String.format("Formato de archivo no permitido: %s. Formatos permitidos: " +
                    "Imágenes: %s | Videos: %s | Audio: %s",
                    extension,
                    String.join(", ", FORMATOS_IMAGEN),
                    String.join(", ", FORMATOS_VIDEO),
                    String.join(", ", FORMATOS_AUDIO)
                )
            );
        }
    }

    private void validarTamanio(long bytes) {
        long maximo = maxFileSizeMb * 1024 * 1024;
        if (bytes > maximo) {
            throw new IllegalArgumentException(
                String.format("El archivo supera el tamaño máximo permitido de %d MB", maxFileSizeMb));
        }
    }

    private boolean esFormatoPermitido(String extension) {
        extension = extension.toLowerCase();
        for (String formato : FORMATOS_IMAGEN) {
            if (formato.equals(extension)) return true;
        }
        for (String formato : FORMATOS_VIDEO) {
            if (formato.equals(extension)) return true;
        }
        for (String formato : FORMATOS_AUDIO) {
            if (formato.equals(extension)) return true;
        }
        return false;
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
