package ar.utn.ba.ddsi.frontend.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HechoFormDTO {
    private String titulo;
    private String descripcion;
    private Double latitud;
    private Double longitud;
    private String categoria;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaAcontecimiento;
    
    private List<MultipartFile> contenidoMultimedia;
}