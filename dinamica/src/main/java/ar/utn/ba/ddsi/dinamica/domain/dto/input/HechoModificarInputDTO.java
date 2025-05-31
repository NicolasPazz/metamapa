package ar.utn.ba.ddsi.dinamica.domain.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class HechoModificarInputDTO {
    @JsonProperty("id_contribuyente")
    private String idContribuyente;
    @JsonProperty("titulo")
    private String nuevoTitulo;
    @JsonProperty("descripcion")
    private String nuevoDescripcion;
    @JsonProperty("categoria")
    private String nuevoNombreCategoria;
    @JsonProperty("latitud")
    private double nuevoLatitud;
    @JsonProperty("longitud")
    private double nuevoLongitud;
    @JsonProperty("fecha_de_acontecimiento")
    private String nuevoFechaDeAcontecimiento;
    @JsonProperty("multimedia")
    private List<String> nuevoContenidoMultimedia;
}
