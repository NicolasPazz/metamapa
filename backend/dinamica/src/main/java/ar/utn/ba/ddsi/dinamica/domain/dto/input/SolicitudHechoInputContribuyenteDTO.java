package ar.utn.ba.ddsi.dinamica.domain.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class SolicitudHechoInputContribuyenteDTO {
    @JsonProperty("id_contribuyente")
    private Long idContribuyente; // Null para anónimos

    @NotBlank(message = "El título es obligatorio")
    @JsonProperty("titulo")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    @JsonProperty("descripcion")
    private String descripcion;

    @NotBlank(message = "La categoría es obligatoria")
    @JsonProperty("categoria")
    private String categoria;

    @NotNull(message = "La latitud es obligatoria")
    @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
    @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
    @JsonProperty("latitud")
    private Double latitud;

    @NotNull(message = "La longitud es obligatoria")
    @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
    @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
    @JsonProperty("longitud")
    private Double longitud;

    @NotBlank(message = "La fecha de acontecimiento es obligatoria")
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "La fecha debe estar en formato yyyy-MM-dd")
    @JsonProperty("fecha_acontecimiento")
    private String fechaDeAcontecimiento;

    @Valid
    @JsonProperty("multimedia")
    private List<ContenidoMultimediaInputDTO> multimedia;
}
