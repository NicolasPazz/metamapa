package ar.utn.ba.ddsi.dinamica.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class MultimediaStorageService {

    @Value("${multimedia.storage.path:backend/data/multimedia}")
    private String storagePath;

    // Formatos permitidos según especificación
    private static final String[] FORMATOS_IMAGEN = {"jpg", "jpeg", "png", "gif", "webp"};
    private static final String[] FORMATOS_VIDEO = {"mp4", "webm"};
    private static final String[] FORMATOS_AUDIO = {"mp3", "wav", "ogg"};

    public String guardarArchivo(MultipartFile archivo) throws IOException {
        if (archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        String nombreOriginal = archivo.getOriginalFilename();
        if (nombreOriginal == null || !nombreOriginal.contains(".")) {
            throw new IllegalArgumentException("El archivo debe tener una extensión válida");
        }

        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".") + 1).toLowerCase();
        if (!esFormatoPermitido(extension)) {
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

        Path directorio = obtenerDirectorio();
        Files.createDirectories(directorio);

        String nombreArchivo = UUID.randomUUID().toString() + "." + extension;
        Path destino = directorio.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        return nombreArchivo;
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

    public Resource cargarArchivo(String nombreArchivo) throws IOException {
        Path archivo = obtenerDirectorio().resolve(nombreArchivo);
        Resource resource = new UrlResource(archivo.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("No se pudo leer el archivo: " + nombreArchivo);
        }
    }

    private Path obtenerDirectorio() {
        return Paths.get(storagePath);
    }
}

