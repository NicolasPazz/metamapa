package ar.utn.ba.ddsi.estatica.service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class AsyncProcessingService {

  private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
    Thread thread = new Thread(r);
    thread.setName("csv-processor");
    thread.setDaemon(true);
    return thread;
  });

  public void ejecutarAsync(Runnable tarea) {
    executor.submit(() -> {
      try {
        tarea.run();
      } catch (Exception e) {
        log.error("Error en tarea asíncrona: {}", e.getMessage(), e);
      }
    });
  }

  @PreDestroy
  public void shutdown() {
    log.info("Cerrando executor de procesamiento...");
    executor.shutdown();
    try {
      if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}

