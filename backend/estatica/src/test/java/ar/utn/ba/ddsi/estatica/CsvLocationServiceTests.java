package ar.utn.ba.ddsi.estatica;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ar.utn.ba.ddsi.estatica.service.CsvLocationService;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CsvLocationServiceTests {

  @Autowired
  private CsvLocationService csvLocationService;

  @Test
  void savedCsvIsListedAndCanBeRead() throws IOException {
    byte[] contenido = "titulo,descripcion\nA,B\n".getBytes();
    csvLocationService.guardarArchivo("prueba.csv", new ByteArrayInputStream(contenido));

    assertThat(csvLocationService.listarArchivos()).contains("prueba.csv");
    try (InputStream leido = csvLocationService.abrirArchivo("prueba.csv")) {
      assertThat(leido.readAllBytes()).isEqualTo(contenido);
    }
  }

  @Test
  void missingCsvIsNotFound() {
    assertThatThrownBy(() -> csvLocationService.abrirArchivo("no-existe.csv"))
        .isInstanceOf(FileNotFoundException.class);
  }
}
