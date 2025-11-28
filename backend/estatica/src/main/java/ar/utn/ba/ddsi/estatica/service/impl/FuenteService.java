package ar.utn.ba.ddsi.estatica.service.impl;

import ar.utn.ba.ddsi.estatica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.estatica.domain.hecho.Hecho;
import ar.utn.ba.ddsi.estatica.domain.hecho.OrigenHecho;
import ar.utn.ba.ddsi.estatica.domain.parseadorCSV;
import ar.utn.ba.ddsi.estatica.domain.repositories.IHechosRepository;
import ar.utn.ba.ddsi.estatica.service.IFuenteService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.MessageDigest;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j

public class FuenteService implements IFuenteService {
  private final IHechosRepository hechosRepository;
  private final Map<String, String> csvHashes = new HashMap<>();
  private final parseadorCSV parseadorCSV;


  @PostConstruct
  // La primera vez tarda 10 minutos en cargar 19740 hechos a la db.
  public void initImportacion() {
    log.info("Iniciando importación automática de CSV al arrancar la aplicación...");
    try {
      importarCsvsSiHay();
    } catch (Exception e) {
      log.error("Error en la importación automática: {}", e.getMessage(), e);
    }
    log.info("Importación automática de CSV finalizada.");
  }


  @Override
  @Transactional
  public List<HechoOutputDTO> getAllHechosNuevos() {
    try {
      importarCsvsSiHay();
    } catch (Exception e) {
      log.warn("Error durante importación lazy de CSVs: {}", e.getMessage(), e);
    }
    List<Hecho> noEnviados = hechosRepository.findByEnviadoFalse();

    noEnviados.forEach(h -> h.setEnviado(Boolean.TRUE));
    hechosRepository.saveAll(noEnviados);

    return noEnviados.stream()
            .map(Hecho::toOutputDTO)
            .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void importarCsvsSiHay() {
    List<Path> csvPaths = parseadorCSV.encontrarArchivosCsv();
    if (csvPaths.isEmpty()) {
      log.info("No hay archivos CSV en ./csv-files ni en classpath:/csv-files");
      return;
    }
    for (Path path : csvPaths) {
      try {
        File file = path.toFile();
        if (!file.exists() || !file.isFile() || !file.canRead()) continue;

        String canonicalPath = file.getCanonicalPath();
        String currentHash = parseadorCSV.calcularHashArchivo(file);
        String previousHash = csvHashes.get(canonicalPath);

        if (previousHash != null && previousHash.equals(currentHash)) {
          log.info("CSV '{}' sin cambios, se salta", path);
          log.info("Hash previo: {}, hash actual: {}", previousHash, currentHash);
          continue;
        }

        csvHashes.put(canonicalPath, currentHash);
        List<Hecho> hechos_parsed = parseadorCSV.parsearCsvAHechos(path);
        log.info("Parseados {} registros desde '{}'", hechos_parsed.size(), path);

        int processed = 0;
        for (Hecho csvHecho : hechos_parsed) {
          processed++;
          String titulo = csvHecho.getTitulo() == null ? "" : csvHecho.getTitulo().trim();
          if (titulo.isEmpty()) {
            log.warn("Fila {} en '{}' sin título — se omite", processed, path);
            continue;
          }

          Optional<Hecho> optExistente = hechosRepository.findFirstByTituloIgnoreCase(titulo);
          if (optExistente.isPresent()) {
            Hecho existente = optExistente.get();
            boolean changed = parseadorCSV.actualizarSiCambio(existente, csvHecho);
            if (changed) {
              existente.setEnviado(Boolean.FALSE);
              existente.setFechaDeCarga(LocalDateTime.now());
              hechosRepository.save(existente);
              log.debug("Hecho '{}' actualizado y marcado como NO enviado", titulo);
            } else {
              log.debug("Hecho '{}' sin cambios — no se actualiza ni marca", titulo);
            }
          } else {
            Hecho nuevo = csvHecho;
            nuevo.setId(null);
            if (nuevo.getOrigen() == null) nuevo.setOrigen(OrigenHecho.DATASET);
            nuevo.setEnviado(Boolean.FALSE);
            if (nuevo.getFechaDeCarga() == null) nuevo.setFechaDeCarga(LocalDateTime.now());
            hechosRepository.save(nuevo);
          }
        }

        log.info("Procesadas {} filas desde '{}'", hechos_parsed.size(), path);

      } catch (Exception e) {
        log.warn("Fallo importando '{}': {}", path, e.getMessage(), e);
      }
    }
  }


  private List<Hecho> parsearMultipartAHechos(MultipartFile file) throws Exception {
    // Convertimos el flujo de bytes del upload en un Reader de texto
    try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {

      CsvToBean<Hecho> csvToBean = new CsvToBeanBuilder<Hecho>(reader)
              .withType(Hecho.class)
              .withIgnoreLeadingWhiteSpace(true)
              .withSeparator(',') // Asegúrate que sea coma o punto y coma según tu CSV
              .build();

      return csvToBean.parse();
    }
  }


  private void saveListaHechos(List<Hecho> hechos) {
    int processed = 0;

    for (Hecho csvHecho : hechos) {
      processed++;
      String titulo = csvHecho.getTitulo() == null ? "" : csvHecho.getTitulo().trim();

      if (titulo.isEmpty()) {
        log.warn("Fila {} sin título — se omite", processed);
        continue;
      }
      Optional<Hecho> optExistente = hechosRepository.findFirstByTituloIgnoreCase(titulo);
      if (optExistente.isPresent()) {
        Hecho existente = optExistente.get();
        boolean changed = parseadorCSV.actualizarSiCambio(existente, csvHecho); // Tu método existente

        if (changed) {
          existente.setEnviado(Boolean.FALSE);
          existente.setFechaDeCarga(LocalDateTime.now());
          hechosRepository.save(existente);
          log.debug("Hecho '{}' actualizado", titulo);
        }
      } else {
        // Lógica de creación
        Hecho nuevo = csvHecho;
        nuevo.setId(null); // Aseguramos que sea nuevo
        if (nuevo.getOrigen() == null) nuevo.setOrigen(OrigenHecho.DATASET);
        nuevo.setEnviado(Boolean.FALSE);
        if (nuevo.getFechaDeCarga() == null) nuevo.setFechaDeCarga(LocalDateTime.now());

        hechosRepository.save(nuevo);
      }
    }
    log.info("Procesamiento de lista finalizado. Total procesados: {}", processed);
  }

  @Transactional
  public void procesarCsvUpload(MultipartFile file_csv) {
    log.info("Procesando archivo subido: {}", file_csv.getOriginalFilename());

    try {
      List<Hecho> hechosParseados = parsearMultipartAHechos(file_csv);
      log.info("Se encontraron {} hechos en el archivo. Iniciando guardado...", hechosParseados.size());
      saveListaHechos(hechosParseados);

    } catch (Exception e) {
      log.error("Error procesando el archivo subido: {}", e.getMessage(), e);
      throw new RuntimeException("Error al procesar el CSV: " + e.getMessage());
    }
  }

}