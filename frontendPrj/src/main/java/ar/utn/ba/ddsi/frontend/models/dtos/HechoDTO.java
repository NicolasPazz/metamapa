package ar.utn.ba.ddsi.frontend.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Data
public class HechoDTO {
    private Long id;
    private String origen;
    private String estado;
    @JsonProperty("contribuyente_id")
    private Long contribuyente; //   private ContribuyenteDTO contribuyente;
    private List<String> etiquetas;
    private String titulo;
    private String descripcion;
    private String categoria;
    private Double latitud;
    private Double longitud;
    private String provincia;
    @JsonProperty("fecha_acontecimiento")
    private LocalDateTime fechaDeAcontecimiento;
    @JsonProperty("fecha_carga")
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
