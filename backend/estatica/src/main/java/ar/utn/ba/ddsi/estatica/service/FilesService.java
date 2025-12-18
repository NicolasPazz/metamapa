package ar.utn.ba.ddsi.estatica.service;

import ar.utn.ba.ddsi.estatica.domain.Archivo;
import ar.utn.ba.ddsi.estatica.repositorie.IArchivoRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  public void procesarCsvSubido(MultipartFile file) throws Exception {
    validarArchivo(file);

    String nombreArchivo = file.getOriginalFilename();
    csvLocationService.guardarArchivo(nombreArchivo, file.getInputStream());

    registrarArchivo(nombreArchivo);

    if (TransactionSynchronizationManager.isSynchronizationActive()) {
      TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
        @Override
        public void afterCommit() {
          asyncProcessingService.ejecutarAsync(() -> procesarArchivosPendientes());
        }
      });
    } else {
      asyncProcessingService.ejecutarAsync(this::procesarArchivosPendientes);
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
      Archivo archivo = archivoExistente.get();
      archivo.setProcesado(false);
      archivoRepository.save(archivo);
      log.info("[FilesService] Archivo actualizado desde upload: {}", nombreArchivo);
    } else {
      Archivo archivo = Archivo.builder()
          .nombre(nombreArchivo)
          .procesado(false)
          .build();
      archivoRepository.save(archivo);
      log.info("[FilesService] Archivo registrado desde upload: {}", nombreArchivo);
    }
  }
}
