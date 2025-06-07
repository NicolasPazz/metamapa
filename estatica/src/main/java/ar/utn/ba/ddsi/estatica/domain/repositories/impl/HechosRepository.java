package ar.utn.ba.ddsi.estatica.domain.repositories.impl;

import ar.utn.ba.ddsi.estatica.domain.hecho.DataHecho;
import ar.utn.ba.ddsi.estatica.domain.hecho.Hecho;
import ar.utn.ba.ddsi.estatica.domain.hecho.OrigenHecho;
import ar.utn.ba.ddsi.estatica.domain.repositories.IHechosRepository;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

@Repository
public class HechosRepository implements IHechosRepository {

  private final List<Hecho> hechos = new ArrayList<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  public HechosRepository() {
    // Cargar todos los CSV al iniciar el repositorio
    try {
      // Obtener la URL del directorio csv-files desde el classpath
      Path csvDir = Paths.get(getClass().getClassLoader().getResource("csv-files").toURI());

      try (Stream<Path> paths = Files.list(csvDir)) {
        paths
            .filter(Files::isRegularFile)
            .filter(path -> path.toString().endsWith(".csv"))
            .forEach(path -> {
                // Importar hechos por cada archivo CSV
                try {
                  List<DataHecho> nuevosDataHechos = importarHechosPorCSV(path.toString());

                  for (DataHecho dataHecho : nuevosDataHechos) {
                    Hecho hecho = new Hecho(dataHecho, OrigenHecho.DATASET);

                    // Asignar un ID unico al hecho y guardarlo
                    save(hecho);
                  }
                } catch (Exception e) {
                  e.printStackTrace();
                }
            });
      }
    } catch (URISyntaxException | IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public Hecho findById(Long id) {
    return this.hechos.stream()
        .filter(h -> h.getId().equals(id))
        .findFirst()
        .orElse(null);
  }

  @Override
  public List<Hecho> findAll() {
    return this.hechos;
  }

  @Override
  public void save(Hecho hecho) {
    hecho.setId(idGenerator.getAndIncrement());
    this.hechos.add(hecho);
  }

  @Override
  public void update(Hecho hecho) {
    for (int i = 0; i < hechos.size(); i++) {
      if (hechos.get(i).getId().equals(hecho.getId())) {
        hechos.set(i, hecho);
        return;
      }
    }
  }

  @Override
  public void delete(Hecho hecho) {
    this.hechos.remove(hecho);
  }

  public List<DataHecho> importarHechosPorCSV(String path) throws Exception {
    try (FileReader lector = new FileReader(path)) {
      CsvToBean<DataHecho> csvToBean = new CsvToBeanBuilder<DataHecho>(lector)
          .withType(DataHecho.class)
          .withIgnoreLeadingWhiteSpace(true)
          .build();

      return csvToBean.parse();
    } catch (IOException e) {
      throw new RuntimeException("Error al leer el archivo CSV", e);
    }
  }
}
