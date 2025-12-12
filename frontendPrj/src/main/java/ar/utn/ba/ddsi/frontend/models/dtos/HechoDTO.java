package ar.utn.ba.ddsi.frontend.models.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Data
public class HechoDTO {
    private Long id;
    private String origen;
    private String estado;
    private Long contribuyente; //   private ContribuyenteDTO contribuyente;
    private List<String> etiquetas;
    private String titulo;
    private String descripcion;
    private String categoria;
    private Double latitud;
    private Double longitud;
    private LocalDate fechaDeAcontecimiento;
    private LocalDateTime fechaDeCarga;
    private LocalDateTime ultimaModificacion;
    private List<ContenidoMultimediaDTO> contenidoMultimedia;
    private List<TransicionEstadoHechoDTO> transicionesDeEstado;

    public void FormtoDTO(HechoFormDTO hechoInput) {
        this.setTitulo(hechoInput.getTitulo());
        this.setDescripcion(hechoInput.getDescripcion());
        this.setLatitud(hechoInput.getLatitud());
        this.setLongitud(hechoInput.getLongitud());
        this.setContenidoMultimedia(
                this.fileToMultimediaDTO(hechoInput.getContenidoMultimedia()));
    }

    public List<ContenidoMultimediaDTO> fileToMultimediaDTO(MultipartFile file) {
        List<ContenidoMultimediaDTO> MultimediaLista = new ArrayList<>();
        ContenidoMultimediaDTO contenidoMultimediaDTO = new ContenidoMultimediaDTO();
        contenidoMultimediaDTO.setTipoContenido(file.getContentType());
        contenidoMultimediaDTO.setPathArchivo(file.getOriginalFilename());
        MultimediaLista.add(contenidoMultimediaDTO);
        return MultimediaLista;
    }
}
