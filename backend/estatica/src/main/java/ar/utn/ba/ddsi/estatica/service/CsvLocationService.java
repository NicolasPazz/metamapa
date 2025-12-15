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

  @Value("${csv.storage.path:data/uploads}")
  private String csvStoragePath;

  public InputStream abrirArchivo(String nombreArchivo) throws IOException {
    Path rutaArchivo = obtenerDirectorio().resolve(nombreArchivo);

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
    Path directorio = obtenerDirectorio();
    Files.createDirectories(directorio);

    Path destino = directorio.resolve(nombreArchivo);
    Files.copy(contenido, destino, StandardCopyOption.REPLACE_EXISTING);

    log.info("Archivo guardado: {}", nombreArchivo);
  }

  private Path obtenerDirectorio() {
    return Paths.get(csvStoragePath);
  }
}


