package ar.utn.ba.ddsi.dinamica.domain.dto.output;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HechoOutputDTO {
    private Long id;
    @JsonProperty("id_contribuyente")
    private Long idContribuyente;
    private String origen;
    private String titulo;
    private String descripcion;
    @JsonProperty("id_categoria")
    private String categoria;
    private double latitud;
    private double longitud;
    @JsonProperty("fecha_de_acontecimiento")
    private String fechaDeAcontecimiento;
    @JsonProperty("multimedia_paths")
    private List<String> multimediaPaths;
    @JsonProperty("fecha_de_carga")
    private String fechaDeCarga;
}