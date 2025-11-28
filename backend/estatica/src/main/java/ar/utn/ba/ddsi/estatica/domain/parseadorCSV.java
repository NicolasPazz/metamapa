package ar.utn.ba.ddsi.estatica.domain;

import ar.utn.ba.ddsi.estatica.domain.hecho.Hecho;
import ar.utn.ba.ddsi.estatica.service.impl.FuenteService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class parseadorCSV {


    public List<Hecho> parsearCsvAHechos(Path path) throws Exception {
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



    public List<Path> encontrarArchivosCsv() {
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
                    log.info("Encontrado directorio CSV en: {}", candidate.toAbsolutePath());
                    try (Stream<Path> s = Files.list(candidate)) {
                        return s.filter(Files::isRegularFile)
                                .filter(pp -> pp.toString().toLowerCase().endsWith(".csv"))
                                .collect(Collectors.toList());
                    } catch (Exception e) {
                        log.warn("Error listando CSVs en {}: {}", candidate, e.getMessage(), e);
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
                    log.debug("No se pudo listar csv-files en classpath: {}", e.getMessage());
                }
            }
        } catch (Exception ignored) {
        }

        log.warn("No se encontró el directorio 'csv-files' subiendo desde los puntos de inicio (user.dir, code location, .) ni en classpath.");
        return List.of();
    }


    // Metodos extras
    public String normalize(String s) {
        if (s == null) return null;
        String trimmed = s.trim().toLowerCase();
        return Normalizer.normalize(trimmed, Normalizer.Form.NFKC);
    }
    public boolean doubleChanged(Double a, Double b) {
        if (a == null && b == null) return false;
        if (a == null || b == null) return true;
        return Math.abs(a - b) > 1e-6; // tolerancia 0.000001
    }
    public String calcularHashArchivo(File file) throws Exception {
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

}
