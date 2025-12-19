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
        // Validar nombre de archivo para prevenir path traversal
        validarNombreArchivo(nombreArchivo);
        
        Path directorio = obtenerDirectorio();
        Path archivo = directorio.resolve(nombreArchivo);
        
        log.debug("Intentando cargar archivo: {} desde directorio: {}", nombreArchivo, directorio);
        log.debug("Path completo del archivo: {}", archivo.toAbsolutePath());
        log.debug("Archivo existe: {}", java.nio.file.Files.exists(archivo));
        
        // Validar que el archivo esté dentro del directorio permitido (prevenir path traversal)
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
            // Listar archivos en el directorio para debug
            try {
                if (java.nio.file.Files.exists(directorio)) {
                    log.debug("Archivos en directorio {}: {}", directorio, 
                        java.nio.file.Files.list(directorio).map(Path::getFileName).map(Path::toString).collect(java.util.stream.Collectors.joining(", ")));
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
        
        // Prevenir path traversal y rutas absolutas
        if (nombreArchivo.contains("..") || nombreArchivo.contains("/") || nombreArchivo.contains("\\")) {
            throw new SecurityException("Nombre de archivo inválido: contiene caracteres no permitidos");
        }
        
        // Validar formato UUID.extension (los archivos se guardan con UUID)
        if (!nombreArchivo.matches("^[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}\\.[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("Nombre de archivo inválido: formato esperado UUID.extension");
        }
    }

    private Path obtenerDirectorio() {
        Path path = Paths.get(storagePath);
        // Si el path es relativo, hacerlo absoluto basado en el directorio del proyecto
        if (!path.isAbsolute()) {
            // Obtener el directorio actual
            String userDir = System.getProperty("user.dir");
            Path currentDir = Paths.get(userDir);
            log.debug("Directorio de trabajo actual: {}", currentDir);
            
            // Estrategia: buscar el directorio que contiene "backend" como subdirectorio
            Path projectRoot = currentDir;
            int maxDepth = 10;
            int depth = 0;
            
            // Primero, verificar si el directorio actual contiene "backend"
            Path backendDir = currentDir.resolve("backend");
            if (java.nio.file.Files.exists(backendDir) && java.nio.file.Files.isDirectory(backendDir)) {
                projectRoot = currentDir;
                log.debug("Encontrado 'backend' como subdirectorio de: {}", currentDir);
            } else {
                // Si no, subir hasta encontrar un directorio que contenga "backend"
                while (depth < maxDepth && projectRoot != null && projectRoot.getParent() != null) {
                    backendDir = projectRoot.resolve("backend");
                    if (java.nio.file.Files.exists(backendDir) && java.nio.file.Files.isDirectory(backendDir)) {
                        log.debug("Encontrado 'backend' como subdirectorio de: {}", projectRoot);
                        break;
                    }
                    Path fileName = projectRoot.getFileName();
                    if (fileName != null && "backend".equals(fileName.toString())) {
                        // Estamos dentro de "backend", subir al padre
                        projectRoot = projectRoot.getParent();
                        log.debug("Estamos en 'backend', subiendo a: {}", projectRoot);
                        break;
                    }
                    projectRoot = projectRoot.getParent();
                    depth++;
                }
            }
            
            // Si no encontramos nada, usar el directorio actual
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

