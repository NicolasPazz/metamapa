package ar.utn.ba.ddsi.agregador.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MultimediaStorageService {

    @Value("${multimedia.storage.path:backend/data/multimedia}")
    private String storagePath;

    public Resource cargarArchivo(String nombreArchivo) throws IOException {
        validarNombreArchivo(nombreArchivo);
        
        Path directorio = obtenerDirectorio();
        Path archivo = directorio.resolve(nombreArchivo);
        
        log.debug("Intentando cargar archivo: {} desde directorio: {}", nombreArchivo, directorio);
        log.debug("Path completo del archivo: {}", archivo.toAbsolutePath());
        log.debug("Archivo existe: {}", Files.exists(archivo));
        
        Path directorioBase = directorio.toAbsolutePath().normalize();
        Path archivoNormalizado = archivo.toAbsolutePath().normalize();
        if (!archivoNormalizado.startsWith(directorioBase)) {
            throw new SecurityException("Intento de acceso a archivo fuera del directorio permitido: " + nombreArchivo);
        }
        
        Resource resource = new UrlResource(archivo.toUri());

        if (resource.exists() && resource.isReadable()) {
            log.debug("Archivo cargado exitosamente: {}", archivo);
            return resource;
        } else {
            log.error("No se pudo leer el archivo: {} desde: {}", nombreArchivo, archivo.toAbsolutePath());
            try {
                if (Files.exists(directorio)) {
                    try (var stream = Files.list(directorio)) {
                        log.debug("Archivos en directorio {}: {}", directorio, 
                            stream.map(Path::getFileName).map(Path::toString).collect(Collectors.joining(", ")));
                    }
                } else {
                    log.error("El directorio no existe: {}", directorio.toAbsolutePath());
                }
            } catch (Exception e) {
                log.error("Error listando archivos en directorio: {}", e.getMessage());
            }
            throw new IOException("No se pudo leer el archivo: " + nombreArchivo);
        }
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

    private Path obtenerDirectorio() {
        Path path = Paths.get(storagePath);
        if (!path.isAbsolute()) {
            String userDir = System.getProperty("user.dir");
            Path currentDir = Paths.get(userDir);
            log.debug("Directorio de trabajo actual: {}", currentDir);
            
            Path projectRoot = currentDir;
            int maxDepth = 10;
            int depth = 0;
            
            Path backendDir = currentDir.resolve("backend");
            if (Files.exists(backendDir) && Files.isDirectory(backendDir)) {
                projectRoot = currentDir;
                log.debug("Encontrado 'backend' como subdirectorio de: {}", currentDir);
            } else {
                while (depth < maxDepth && projectRoot != null && projectRoot.getParent() != null) {
                    backendDir = projectRoot.resolve("backend");
                    if (Files.exists(backendDir) && Files.isDirectory(backendDir)) {
                        log.debug("Encontrado 'backend' como subdirectorio de: {}", projectRoot);
                        break;
                    }
                    Path fileName = projectRoot.getFileName();
                    if (fileName != null && "backend".equals(fileName.toString())) {
                        projectRoot = projectRoot.getParent();
                        log.debug("Estamos en 'backend', subiendo a: {}", projectRoot);
                        break;
                    }
                    projectRoot = projectRoot.getParent();
                    depth++;
                }
            }
            
            if (projectRoot == null || depth >= maxDepth) {
                log.warn("No se pudo determinar el root del proyecto, usando directorio actual: {}", currentDir);
                projectRoot = currentDir;
            }
            
            path = projectRoot.resolve(storagePath).toAbsolutePath().normalize();
            log.info("Path final de multimedia (agregador): {}", path);
        }
        return path;
    }
}

