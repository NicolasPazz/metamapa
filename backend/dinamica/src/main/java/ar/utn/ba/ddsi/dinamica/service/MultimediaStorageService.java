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
        log.debug("Guardando archivo en directorio: {}", directorio.toAbsolutePath());

        String nombreArchivo = UUID.randomUUID().toString() + "." + extension;
        Path destino = directorio.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        log.info("Archivo guardado exitosamente: {} en {}", nombreArchivo, destino.toAbsolutePath());

        return nombreArchivo;
    }

    public String guardarArchivoDesdeBase64(String contenidoBase64, String nombreOriginal, String tipoContenido) throws IOException {
        log.debug("guardarArchivoDesdeBase64 - Inicio - nombreOriginal: {}, tipoContenido: {}, tamaño base64: {}", 
            nombreOriginal, tipoContenido, contenidoBase64 != null ? contenidoBase64.length() : 0);
        
        if (contenidoBase64 == null || contenidoBase64.isEmpty()) {
            throw new IllegalArgumentException("El contenido base64 está vacío");
        }

        String extension = null;
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".") + 1).toLowerCase();
            log.debug("Extensión inferida desde nombreOriginal: {}", extension);
        } else {
            if (tipoContenido != null) {
                switch (tipoContenido.toUpperCase()) {
                    case "IMAGEN":
                        extension = "jpg";
                        break;
                    case "VIDEO":
                        extension = "mp4";
                        break;
                    case "AUDIO":
                        extension = "mp3";
                        break;
                }
                log.debug("Extensión inferida desde tipoContenido: {}", extension);
            }
        }

        if (extension == null || !esFormatoPermitido(extension)) {
            log.error("Formato de archivo no permitido: {}", extension);
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
        log.info("Directorio obtenido: {}", directorio.toAbsolutePath());
        
        Files.createDirectories(directorio);
        log.debug("Directorio creado/verificado: {}", directorio.toAbsolutePath());

        String nombreArchivo = UUID.randomUUID().toString() + "." + extension;
        Path destino = directorio.resolve(nombreArchivo);
        log.info("Guardando archivo desde base64 - nombre: {}, destino: {}", nombreArchivo, destino.toAbsolutePath());
        
        try {
            byte[] bytes = java.util.Base64.getDecoder().decode(contenidoBase64);
            log.debug("Base64 decodificado, tamaño bytes: {}", bytes.length);
            
            Files.write(destino, bytes);
            log.info("Archivo guardado desde base64 exitosamente: {} en {} ({} bytes)", 
                nombreArchivo, destino.toAbsolutePath(), bytes.length);
            
            if (Files.exists(destino)) {
                long fileSize = Files.size(destino);
                log.info("Archivo verificado - existe: true, tamaño: {} bytes", fileSize);
            } else {
                log.error("ERROR: Archivo no existe después de escribirlo: {}", destino.toAbsolutePath());
            }
        } catch (Exception e) {
            log.error("Error escribiendo archivo: {}", e.getMessage(), e);
            throw e;
        }

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
        validarNombreArchivo(nombreArchivo);
        
        Path archivo = obtenerDirectorio().resolve(nombreArchivo);
        
        Path directorioBase = obtenerDirectorio().toAbsolutePath().normalize();
        Path archivoNormalizado = archivo.toAbsolutePath().normalize();
        if (!archivoNormalizado.startsWith(directorioBase)) {
            throw new SecurityException("Intento de acceso a archivo fuera del directorio permitido: " + nombreArchivo);
        }
        
        Resource resource = new UrlResource(archivo.toUri());

        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
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
            if (java.nio.file.Files.exists(backendDir) && java.nio.file.Files.isDirectory(backendDir)) {
                projectRoot = currentDir;
                log.debug("Encontrado 'backend' como subdirectorio de: {}", currentDir);
            } else {
                while (depth < maxDepth && projectRoot != null && projectRoot.getParent() != null) {
                    backendDir = projectRoot.resolve("backend");
                    if (java.nio.file.Files.exists(backendDir) && java.nio.file.Files.isDirectory(backendDir)) {
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
            log.info("Path final de multimedia (dinámica): {}", path);
        }
        return path;
    }
}

