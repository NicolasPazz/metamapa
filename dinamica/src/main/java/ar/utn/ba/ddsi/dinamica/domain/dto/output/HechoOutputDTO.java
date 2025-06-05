package ar.utn.ba.ddsi.dinamica.domain.dto.output;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HechoOutputDTO {
    private Long id;
    private String origen;
    private String estado;
    @JsonProperty("id_contribuyente")
    private Long idContribuyente;
    @JsonProperty("id_etiquetas")
    private List<Long> idEtiquetas;
    private String titulo;
    private String descripcion;
    @JsonProperty("id_categoria")
    private String categoria;
    private double latitud;
    private double longitud;
    @JsonProperty("fecha_de_acontecimiento")
    private String fechaDeAcontecimiento;
    @JsonProperty("fecha_de_carga")
    private String fechaDeCarga;
    @JsonProperty("multimedia_paths")
    private List<String> multimediaPaths;   //TODO:podria agregar dos listas una para borrar y otra para agregar
}