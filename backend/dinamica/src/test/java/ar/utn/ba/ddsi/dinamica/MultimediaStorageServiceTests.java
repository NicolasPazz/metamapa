package ar.utn.ba.ddsi.dinamica;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ar.utn.ba.ddsi.dinamica.service.MultimediaStorageService;
import java.io.IOException;
import java.util.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

@SpringBootTest
class MultimediaStorageServiceTests {

  @Autowired
  private MultimediaStorageService storageService;

  @Test
  void savedFileCanBeLoadedFromTheDatabase() throws IOException {
    byte[] contenido = {1, 2, 3, 4};
    String nombre = storageService.guardarArchivo(
        new MockMultipartFile("archivo", "foto.png", "image/png", contenido));

    Resource recurso = storageService.cargarArchivo(nombre);

    assertThat(nombre).endsWith(".png");
    assertThat(recurso.getContentAsByteArray()).isEqualTo(contenido);
  }

  @Test
  void base64FileIsDecodedAndSaved() throws IOException {
    byte[] contenido = "audio".getBytes();
    String nombre = storageService.guardarArchivoDesdeBase64(
        Base64.getEncoder().encodeToString(contenido), "nota.mp3", "AUDIO");

    assertThat(storageService.cargarArchivo(nombre).getContentAsByteArray()).isEqualTo(contenido);
  }

  @Test
  void unknownFileIsNotFound() {
    assertThatThrownBy(() -> storageService.cargarArchivo("00000000-0000-0000-0000-000000000000.png"))
        .isInstanceOf(IOException.class);
  }

  @Test
  void disallowedFormatIsRejected() {
    assertThatThrownBy(() -> storageService.guardarArchivo(
        new MockMultipartFile("archivo", "script.exe", "application/octet-stream", new byte[] {1})))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
