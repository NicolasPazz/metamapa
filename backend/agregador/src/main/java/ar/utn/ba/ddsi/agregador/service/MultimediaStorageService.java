package ar.utn.ba.ddsi.agregador.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class MultimediaStorageService {

    @Value("${multimedia.storage.path:backend/data/multimedia}")
    private String storagePath;

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

