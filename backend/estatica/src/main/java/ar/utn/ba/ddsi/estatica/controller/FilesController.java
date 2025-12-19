package ar.utn.ba.ddsi.estatica.controller;

import ar.utn.ba.ddsi.estatica.dto.input.CsvUploadRequestDTO;
import ar.utn.ba.ddsi.estatica.service.FilesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/csv")
@RequiredArgsConstructor
public class FilesController {

  private final FilesService filesService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> cargarCsv(@ModelAttribute CsvUploadRequestDTO request) {
    try {
      return filesService.procesarCsvSubido(request.getArchivo());
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      log.error("Error procesando CSV: {}", e.getMessage(), e);
      return ResponseEntity.internalServerError().body("Error al procesar el archivo");
    }
  }
}
