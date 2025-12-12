package ar.utn.ba.ddsi.dinamica.domain.dto.input;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SolicitudHechoInputContribuyenteDTO {
    @JsonProperty("id_contribuyente")
    private Long idContribuyente;
    @JsonProperty("titulo")
    private String titulo;
    @JsonProperty("descripcion")
    private String descripcion;
    @JsonProperty("categoria")
    private String categoria;
    @JsonProperty("latitud")
    private double latitud;
    @JsonProperty("longitud")
    private double longitud;
    @JsonProperty("fecha_acontecimiento")
    private String fechaDeAcontecimiento;
    @JsonProperty("multimedia")
    private List<ContenidoMultimediaInputDTO> multimedia;
}
