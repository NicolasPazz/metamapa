package ar.utn.ba.ddsi.estatica.service;

import ar.utn.ba.ddsi.estatica.domain.Archivo;
import ar.utn.ba.ddsi.estatica.repositorie.IArchivoRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilesService {

  private final IArchivoRepository archivoRepository;
  private final CsvLocationService csvLocationService;
  private final CsvProcessingService csvProcessingService;
  private final AsyncProcessingService asyncProcessingService;

  @PostConstruct
  public void inicializar() {
    registrarArchivosExistentes();
    asyncProcessingService.ejecutarAsync(this::procesarArchivosPendientes);
  }

  @Transactional
  protected void registrarArchivosExistentes() {
    log.info("[FilesService] Registrando archivos existentes en disco...");
    List<String> archivosEnDisco = csvLocationService.listarArchivos();

    archivosEnDisco.forEach(nombreArchivo -> {
      if (archivoRepository.findByNombre(nombreArchivo).isEmpty()) {
        Archivo archivo = Archivo.builder()
            .nombre(nombreArchivo)
            .procesado(false)
            .build();
        archivoRepository.save(archivo);
        log.info("[FilesService] Archivo registrado desde disco: {}", nombreArchivo);
      }
    });
  }

  public void procesarArchivosPendientes() {
    List<Archivo> archivosPendientes = archivoRepository.findByProcesadoFalse();

    if (archivosPendientes.isEmpty()) {
      log.info("No hay archivos pendientes de procesamiento");
      return;
    }

    log.info("Procesando {} archivo(s) pendiente(s)", archivosPendientes.size());
    archivosPendientes.forEach(csvProcessingService::procesarArchivo);
  }

  @Transactional
  public ResponseEntity<String> procesarCsvSubido(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return ResponseEntity.badRequest().body("No se recibió ningún archivo");
    }

    validarArchivo(file);

    try {
      String nombreArchivo = file.getOriginalFilename();
      csvLocationService.guardarArchivo(nombreArchivo, file.getInputStream());

      registrarArchivo(nombreArchivo);

      // Procesar archivo de forma asíncrona después de que la transacción se confirme
      // Esto evita procesar archivos que no se guardaron correctamente
      if (TransactionSynchronizationManager.isSynchronizationActive()) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            asyncProcessingService.ejecutarAsync(() -> procesarArchivosPendientes());
          }
        });
      } else {
        // Si no hay transacción activa, procesar directamente
        asyncProcessingService.ejecutarAsync(this::procesarArchivosPendientes);
      }

      return ResponseEntity.ok("Archivo cargado correctamente.");
    } catch (Exception e) {
      log.error("Error procesando CSV: {}", e.getMessage(), e);
      throw new RuntimeException("Error al procesar el archivo: " + e.getMessage(), e);
    }
  }

  private void validarArchivo(MultipartFile file) {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("El archivo está vacío");
    }

    String nombreArchivo = file.getOriginalFilename();
    if (nombreArchivo == null || !nombreArchivo.toLowerCase().endsWith(".csv")) {
      throw new IllegalArgumentException("El archivo debe ser un CSV");
    }
  }

  @Transactional
  protected void registrarArchivo(String nombreArchivo) {
    Optional<Archivo> archivoExistente = archivoRepository.findByNombre(nombreArchivo);

    if (archivoExistente.isPresent()) {
      // Archivo ya existe: marcarlo como no procesado para reprocesarlo
      Archivo archivo = archivoExistente.get();
      archivo.setProcesado(false);
      archivoRepository.save(archivo);
    } else {
      // Archivo nuevo: crear registro en BD para tracking
      Archivo archivo = Archivo.builder()
          .nombre(nombreArchivo)
          .procesado(false)
          .build();
      archivoRepository.save(archivo);
    }
  }
}
