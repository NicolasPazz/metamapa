package ar.utn.ba.ddsi.estatica.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class CsvLocationService {

  @Value("${csv.storage.path:backend/data/csv}")
  private String csvStoragePath;

  public InputStream abrirArchivo(String nombreArchivo) throws IOException {
    validarNombreArchivo(nombreArchivo);
    
    Path rutaArchivo = obtenerDirectorio().resolve(nombreArchivo);
    
    Path directorioBase = obtenerDirectorio().toAbsolutePath().normalize();
    Path archivoNormalizado = rutaArchivo.toAbsolutePath().normalize();
    if (!archivoNormalizado.startsWith(directorioBase)) {
      throw new SecurityException("Intento de acceso a archivo fuera del directorio permitido: " + nombreArchivo);
    }

    if (!Files.exists(rutaArchivo)) {
      throw new FileNotFoundException("Archivo no encontrado: " + nombreArchivo);
    }

    return Files.newInputStream(rutaArchivo);
  }

  public List<String> listarArchivos() {
    Path directorio = obtenerDirectorio();

    if (!Files.exists(directorio) || !Files.isDirectory(directorio)) {
      log.warn("Directorio no existe: {}", directorio);
      return Collections.emptyList();
    }

    try (Stream<Path> archivos = Files.list(directorio)) {
      List<String> csvs = archivos
          .filter(Files::isRegularFile)
          .filter(path -> path.toString().toLowerCase().endsWith(".csv"))
          .map(path -> path.getFileName().toString())
          .collect(Collectors.toList());

      log.info("Encontrados {} archivos CSV", csvs.size());
      return csvs;
    } catch (IOException e) {
      log.error("Error listando archivos: {}", e.getMessage());
      return Collections.emptyList();
    }
  }

  public void guardarArchivo(String nombreArchivo, InputStream contenido) throws IOException {
    validarNombreArchivo(nombreArchivo);
    
    Path directorio = obtenerDirectorio();
    Files.createDirectories(directorio);

    Path destino = directorio.resolve(nombreArchivo);
    
    Path directorioBase = directorio.toAbsolutePath().normalize();
    Path destinoNormalizado = destino.toAbsolutePath().normalize();
    if (!destinoNormalizado.startsWith(directorioBase)) {
      throw new SecurityException("Intento de guardar archivo fuera del directorio permitido: " + nombreArchivo);
    }
    
    Files.copy(contenido, destino, StandardCopyOption.REPLACE_EXISTING);

    log.info("Archivo guardado: {}", nombreArchivo);
  }

  private void validarNombreArchivo(String nombreArchivo) {
    if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
      throw new IllegalArgumentException("El nombre del archivo no puede estar vacío");
    }
    
    if (nombreArchivo.contains("..") || nombreArchivo.contains("/") || nombreArchivo.contains("\\")) {
      throw new SecurityException("Nombre de archivo inválido: contiene caracteres no permitidos");
    }
    
    if (!nombreArchivo.matches("^[a-zA-Z0-9._-]+\\.csv$")) {
      throw new IllegalArgumentException("Nombre de archivo inválido: solo se permiten caracteres alfanuméricos, puntos, guiones y guiones bajos, y debe terminar en .csv");
    }
  }

  private Path obtenerDirectorio() {
    return Paths.get(csvStoragePath);
  }
}


