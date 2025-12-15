package ar.utn.ba.ddsi.dinamica.domain.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;

@Data
public class SolicitudHechoInputContribuyenteDTO {
    @JsonProperty("id_contribuyente")
    private Long idContribuyente;

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

    @NotNull(message = "La fecha de acontecimiento es obligatoria")
    @JsonProperty("fecha_acontecimiento")
    private LocalDate fechaDeAcontecimiento;

    @Valid
    @JsonProperty("multimedia")
    private List<ContenidoMultimediaInputDTO> multimedia;
}
