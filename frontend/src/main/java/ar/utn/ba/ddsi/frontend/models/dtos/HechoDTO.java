package ar.utn.ba.ddsi.frontend.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Data
public class HechoDTO {
    private Long id;
    private String origen;
    private String estado;
    @JsonProperty("contribuyente_id")
    private Long contribuyente;
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
    @JsonProperty("ultima_modificacion")
    private LocalDateTime ultimaModificacion;
    @JsonProperty("multimedia")
    private List<ContenidoMultimediaDTO> contenidoMultimedia;
    @JsonProperty("transiciones")
    private List<TransicionEstadoHechoDTO> transicionesDeEstado;
    @JsonProperty("fuente_id")
    private Long fuenteId;
    @JsonProperty("id_origen")
    private Long idOrigen;
    @JsonProperty("nombre_fuente")
    private String nombreFuente;



}
