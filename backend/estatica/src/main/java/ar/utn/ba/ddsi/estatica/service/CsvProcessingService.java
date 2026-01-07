package ar.utn.ba.ddsi.estatica.service;

import ar.utn.ba.ddsi.estatica.domain.Archivo;
import ar.utn.ba.ddsi.estatica.domain.hecho.Hecho;
import ar.utn.ba.ddsi.estatica.domain.hecho.OrigenHecho;
import ar.utn.ba.ddsi.estatica.repositorie.IArchivoRepository;
import ar.utn.ba.ddsi.estatica.repositorie.IHechosRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvProcessingService {

  private static final int BATCH_SIZE = 50;

  private final IArchivoRepository archivoRepository;
  private final IHechosRepository hechosRepository;
  private final CsvParserService csvParserService;

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public void procesarArchivo(Archivo archivo) {
    try {
      log.info("Procesando archivo: {}", archivo.getNombre());

      int[] contador = {0};
      csvParserService.parsearCsvLazy(archivo.getNombre(), hecho -> {
        guardarHecho(hecho);
        contador[0]++;

        if (contador[0] % BATCH_SIZE == 0) {
          entityManager.flush();
          entityManager.clear();
        }
      });

      entityManager.flush();
      marcarArchivoProcesado(archivo);

      log.info("Archivo procesado: {} ({} hechos)", archivo.getNombre(), contador[0]);
    } catch (Exception e) {
      log.error("Error procesando archivo '{}': {}", archivo.getNombre(), e.getMessage(), e);
      throw new RuntimeException("Error al procesar el archivo: " + archivo.getNombre(), e);
    }
  }

  private void guardarHecho(Hecho csvHecho) {
    String titulo = csvHecho.getTitulo();
    if (titulo == null || titulo.trim().isEmpty()) {
      return;
    }

    Optional<Hecho> hechoExistente = hechosRepository.findFirstByTituloIgnoreCase(titulo.trim());

    if (hechoExistente.isPresent()) {
      actualizarHecho(hechoExistente.get(), csvHecho);
    } else {
      crearNuevoHecho(csvHecho);
    }
  }

  private void actualizarHecho(Hecho existente, Hecho csvHecho) {
    existente.setDescripcion(csvHecho.getDescripcion());
    existente.setCategoria(csvHecho.getCategoria());
    existente.setLatitud(csvHecho.getLatitud());
    existente.setLongitud(csvHecho.getLongitud());
    existente.setFechaDeAcontecimiento(csvHecho.getFechaDeAcontecimiento());
    existente.setFechaDeCarga(LocalDateTime.now());
    existente.setOrigen(OrigenHecho.DATASET);
    existente.setEnviado(false);
    hechosRepository.save(existente);
  }

  private void crearNuevoHecho(Hecho csvHecho) {
    csvHecho.setId(null);
    csvHecho.setOrigen(OrigenHecho.DATASET);
    csvHecho.setEnviado(false);
    csvHecho.setFechaDeCarga(LocalDateTime.now());
    hechosRepository.save(csvHecho);
  }

  private void marcarArchivoProcesado(Archivo archivo) {
    archivo.setProcesado(true);
    archivoRepository.save(archivo);
  }
}

