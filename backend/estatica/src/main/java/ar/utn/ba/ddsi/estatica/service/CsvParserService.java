package ar.utn.ba.ddsi.estatica.service;

import ar.utn.ba.ddsi.estatica.domain.hecho.Hecho;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class CsvParserService {

  private final CsvLocationService csvLocationService;

  public void parsearCsvLazy(String nombreArchivo, Consumer<Hecho> procesador) throws IOException {
    // Parseo lazy: procesar fila por fila sin cargar todo el CSV en memoria
    try (InputStream inputStream = csvLocationService.abrirArchivo(nombreArchivo);
         InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

      CsvToBean<Hecho> csvToBean = new CsvToBeanBuilder<Hecho>(reader)
          .withType(Hecho.class)
          .withIgnoreLeadingWhiteSpace(true)
          .build();

      procesarFilas(csvToBean.iterator(), procesador, nombreArchivo);
    }
  }

  private void procesarFilas(Iterator<Hecho> iterator, Consumer<Hecho> procesador, String nombreArchivo) {
    int filasProcessadas = 0;

    // Procesar fila por fila: si una falla, continuar con la siguiente
    while (iterator.hasNext()) {
      try {
        Hecho hecho = iterator.next();
        procesador.accept(hecho);
        filasProcessadas++;
      } catch (Exception e) {
        // Continuar procesando aunque una fila falle (resiliente a errores)
        log.warn("Error en fila {}: {}", filasProcessadas + 1, e.getMessage());
      }
    }

    log.info("Procesadas {} filas de '{}'", filasProcessadas, nombreArchivo);
  }
}

