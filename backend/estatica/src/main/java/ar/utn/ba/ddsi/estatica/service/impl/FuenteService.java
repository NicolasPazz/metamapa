package ar.utn.ba.ddsi.estatica.service.impl;

import ar.utn.ba.ddsi.estatica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.estatica.domain.hecho.Hecho;
import ar.utn.ba.ddsi.estatica.domain.hecho.OrigenHecho;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FuenteService implements IFuenteService {
  private static final Logger LOGGER = LoggerFactory.getLogger(FuenteService.class);
  private final IHechosRepository hechosRepository;
  private final Map<String, String> csvHashes = new HashMap<>();

  public FuenteService(IHechosRepository hechosRepository) {
    this.hechosRepository = hechosRepository;
  }

  @PostConstruct
  // La primera vez tarda 10 minutos en cargar 19740 hechos a la db.
  public void initImportacion() {
    LOGGER.info("Iniciando importación automática de CSV al arrancar la aplicación...");
    try {
      importarCsvsSiHay();
    } catch (Exception e) {
      LOGGER.error("Error en la importación automática: {}", e.getMessage(), e);
    }
    LOGGER.info("Importación automática de CSV finalizada.");
  }


  @Override
  @Transactional
  public List<HechoOutputDTO> getAllHechosNuevos() {
    try {
      importarCsvsSiHay();
    } catch (Exception e) {
      LOGGER.warn("Error durante importación lazy de CSVs: {}", e.getMessage(), e);
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
    List<Path> csvPaths = encontrarArchivosCsv();
    if (csvPaths.isEmpty()) {
      LOGGER.info("No hay archivos CSV en ./csv-files ni en classpath:/csv-files");
      return;
    }

    for (Path path : csvPaths) {
      try {
        File file = path.toFile();
        if (!file.exists() || !file.isFile() || !file.canRead()) continue;

        String canonicalPath = file.getCanonicalPath();
        String currentHash = calcularHashArchivo(file);
        String previousHash = csvHashes.get(canonicalPath);

        if (previousHash != null && previousHash.equals(currentHash)) {
          LOGGER.info("CSV '{}' sin cambios, se salta", path);
          LOGGER.info("Hash previo: {}, hash actual: {}", previousHash, currentHash);
          continue;
        }

        csvHashes.put(canonicalPath, currentHash);

        List<Hecho> parsed = parsearCsvAHechos(path);
        LOGGER.info("Parseados {} registros desde '{}'", parsed.size(), path);

        int processed = 0;
        for (Hecho csvHecho : parsed) {
          processed++;
          String titulo = csvHecho.getTitulo() == null ? "" : csvHecho.getTitulo().trim();
          if (titulo.isEmpty()) {
            LOGGER.warn("Fila {} en '{}' sin título — se omite", processed, path);
            continue;
          }

          Optional<Hecho> optExistente = hechosRepository.findFirstByTituloIgnoreCase(titulo);
          if (optExistente.isPresent()) {
            Hecho existente = optExistente.get();
            boolean changed = actualizarSiCambio(existente, csvHecho);
            if (changed) {
              existente.setEnviado(Boolean.FALSE);
              existente.setFechaDeCarga(LocalDateTime.now());
              hechosRepository.save(existente);
              LOGGER.debug("Hecho '{}' actualizado y marcado como NO enviado", titulo);
            } else {
              LOGGER.debug("Hecho '{}' sin cambios — no se actualiza ni marca", titulo);
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

        LOGGER.info("Procesadas {} filas desde '{}'", parsed.size(), path);

      } catch (Exception e) {
        LOGGER.warn("Fallo importando '{}': {}", path, e.getMessage(), e);
      }
    }
  }

  private boolean actualizarSiCambio(Hecho existente, Hecho csvHecho) {
    boolean changed = false;

    if (!Objects.equals(normalize(existente.getTitulo()), normalize(csvHecho.getTitulo()))) {
      LOGGER.info("Cambio detectado en 'titulo' para '{}': '{}' -> '{}'",
          existente.getId(), existente.getTitulo(), csvHecho.getTitulo());
      existente.setTitulo(csvHecho.getTitulo());
      changed = true;
    }

    if (!Objects.equals(normalize(existente.getDescripcion()), normalize(csvHecho.getDescripcion()))) {
      LOGGER.info("Cambio detectado en 'descripcion' para '{}': '{}' -> '{}'",
          existente.getId(), existente.getDescripcion(), csvHecho.getDescripcion());
      existente.setDescripcion(csvHecho.getDescripcion());
      changed = true;
    }

    if (!Objects.equals(normalize(existente.getNombreCategoria()), normalize(csvHecho.getNombreCategoria()))) {
      LOGGER.info("Cambio detectado en 'nombreCategoria' para '{}': '{}' -> '{}'",
          existente.getId(), existente.getNombreCategoria(), csvHecho.getNombreCategoria());
      existente.setNombreCategoria(csvHecho.getNombreCategoria());
      changed = true;
    }

    if (doubleChanged(existente.getLatitud(), csvHecho.getLatitud())) {
      LOGGER.info("Cambio detectado en 'latitud' para '{}': '{}' -> '{}'",
          existente.getId(), existente.getLatitud(), csvHecho.getLatitud());
      existente.setLatitud(csvHecho.getLatitud());
      changed = true;
    }

    if (doubleChanged(existente.getLongitud(), csvHecho.getLongitud())) {
      LOGGER.info("Cambio detectado en 'longitud' para '{}': '{}' -> '{}'",
          existente.getId(), existente.getLongitud(), csvHecho.getLongitud());
      existente.setLongitud(csvHecho.getLongitud());
      changed = true;
    }

    if (!Objects.equals(existente.getFechaDeAcontecimiento(), csvHecho.getFechaDeAcontecimiento())) {
      LOGGER.info("Cambio detectado en 'fechaDeAcontecimiento' para '{}': '{}' -> '{}'",
          existente.getId(), existente.getFechaDeAcontecimiento(), csvHecho.getFechaDeAcontecimiento());
      existente.setFechaDeAcontecimiento(csvHecho.getFechaDeAcontecimiento());
      changed = true;
    }

    if (changed) {
      LOGGER.info("Hecho '{}' con titulo '{}' marcado como modificado", existente.getId(), existente.getTitulo());
    }

    return changed;
  }

  private String normalize(String s) {
    if (s == null) return null;
    String trimmed = s.trim().toLowerCase();
    return Normalizer.normalize(trimmed, Normalizer.Form.NFKC);
  }

  private boolean doubleChanged(Double a, Double b) {
    if (a == null && b == null) return false;
    if (a == null || b == null) return true;
    return Math.abs(a - b) > 1e-6; // tolerancia 0.000001
  }

  private String calcularHashArchivo(File file) throws Exception {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    try (InputStream is = Files.newInputStream(file.toPath())) {
      byte[] buffer = new byte[8192];
      int read;
      while ((read = is.read(buffer)) != -1) {
        digest.update(buffer, 0, read);
      }
    }
    return Base64.getEncoder().encodeToString(digest.digest());
  }

  private List<Path> encontrarArchivosCsv() {
    final int MAX_UP = 6;

    List<Path> starts = new ArrayList<>();
    try {
      starts.add(Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize());
    } catch (Exception ignored) {
    }
    try {
      CodeSource codeSource = FuenteService.class.getProtectionDomain().getCodeSource();
      if (codeSource != null && codeSource.getLocation() != null) {
        Path codeLocation = Paths.get(codeSource.getLocation().toURI()).toAbsolutePath().normalize();
        if (Files.isRegularFile(codeLocation)) {
          starts.add(codeLocation.getParent());
        } else {
          starts.add(codeLocation);
        }
      }
    } catch (URISyntaxException ignored) {
    }
    try {
      starts.add(Paths.get(".").toAbsolutePath().normalize());
    } catch (Exception ignored) {
    }

    List<Path> filteredStarts = starts.stream()
        .filter(Objects::nonNull)
        .distinct()
        .toList();

    for (Path start : filteredStarts) {
      Path p = start;
      for (int up = 0; up <= MAX_UP && p != null; up++) {
        Path candidate = p.resolve("csv-files");
        if (Files.exists(candidate) && Files.isDirectory(candidate)) {
          LOGGER.info("Encontrado directorio CSV en: {}", candidate.toAbsolutePath());
          try (Stream<Path> s = Files.list(candidate)) {
            return s.filter(Files::isRegularFile)
                .filter(pp -> pp.toString().toLowerCase().endsWith(".csv"))
                .collect(Collectors.toList());
          } catch (Exception e) {
            LOGGER.warn("Error listando CSVs en {}: {}", candidate, e.getMessage(), e);
            return List.of();
          }
        }
        p = p.getParent();
      }
    }

    try {
      var resource = getClass().getClassLoader().getResource("csv-files");
      if (resource != null) {
        try {
          Path classpathDir = Paths.get(resource.toURI());
          if (Files.exists(classpathDir) && Files.isDirectory(classpathDir)) {
            try (Stream<Path> s = Files.list(classpathDir)) {
              return s.filter(Files::isRegularFile)
                  .filter(p -> p.toString().toLowerCase().endsWith(".csv"))
                  .collect(Collectors.toList());
            }
          }
        } catch (Exception e) {
          LOGGER.debug("No se pudo listar csv-files en classpath: {}", e.getMessage());
        }
      }
    } catch (Exception ignored) {
    }

    LOGGER.warn("No se encontró el directorio 'csv-files' subiendo desde los puntos de inicio (user.dir, code location, .) ni en classpath.");
    return List.of();
  }

  private List<Hecho> parsearCsvAHechos(Path path) throws Exception {
    File file = path.toFile();
    if (file.exists() && file.isFile() && file.canRead()) {
      try (FileReader reader = new FileReader(file)) {
        CsvToBean<Hecho> csvToBean = new CsvToBeanBuilder<Hecho>(reader)
            .withType(Hecho.class)
            .withIgnoreLeadingWhiteSpace(true)
            .build();
        return csvToBean.parse();
      }
    }

    String resourceRel = path.getFileName().toString();
    InputStream is = getClass().getClassLoader().getResourceAsStream("csv-files/" + resourceRel);
    if (is != null) {
      try (InputStreamReader isr = new InputStreamReader(is)) {
        CsvToBean<Hecho> csvToBean = new CsvToBeanBuilder<Hecho>(isr)
            .withType(Hecho.class)
            .withIgnoreLeadingWhiteSpace(true)
            .build();
        return csvToBean.parse();
      }
    }

    throw new IllegalArgumentException("No se pudo leer el CSV: " + path);
  }
}