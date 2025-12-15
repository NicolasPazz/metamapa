package ar.utn.ba.ddsi.estatica.dto.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CsvUploadRequestDTO {
  private MultipartFile archivo;
}

