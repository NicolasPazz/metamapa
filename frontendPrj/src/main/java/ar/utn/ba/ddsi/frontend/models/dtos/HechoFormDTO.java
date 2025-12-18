package ar.utn.ba.ddsi.frontend.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HechoFormDTO {
    public String titulo;
    private String descripcion;
    private Double latitud;
    private Double longitud;
    private String categoria;
    private LocalDateTime fechaAcontecimiento;
    private List<MultipartFile> contenidoMultimedia;
}