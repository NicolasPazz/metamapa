package ar.utn.ba.ddsi.estatica.service;

import ar.utn.ba.ddsi.estatica.domain.ContenidoCsv;
import ar.utn.ba.ddsi.estatica.repositorie.IContenidoCsvRepository;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Almacena los CSV subidos en la base de datos (tabla contenido_csv).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CsvLocationService {

  private final IContenidoCsvRepository contenidoRepository;

  @Transactional(readOnly = true)
  public InputStream abrirArchivo(String nombreArchivo) throws IOException {
    validarNombreArchivo(nombreArchivo);

    ContenidoCsv csv = contenidoRepository.findById(nombreArchivo)
        .orElseThrow(() -> new FileNotFoundException("Archivo no encontrado: " + nombreArchivo));
    return new ByteArrayInputStream(csv.getContenido());
  }

  @Transactional(readOnly = true)
  public List<String> listarArchivos() {
    List<String> csvs = contenidoRepository.findAllNombres();
    log.info("Encontrados {} archivos CSV", csvs.size());
    return csvs;
  }

  @Transactional
  public void guardarArchivo(String nombreArchivo, InputStream contenido) throws IOException {
    validarNombreArchivo(nombreArchivo);

    contenidoRepository.save(ContenidoCsv.builder()
        .nombre(nombreArchivo)
        .fechaSubida(LocalDateTime.now())
        .contenido(contenido.readAllBytes())
        .build());

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
}
