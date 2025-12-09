package ar.utn.ba.ddsi.agregador.domain.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class CrearColeccionInputDTO {

    @NotBlank(message = "El titulo es obligatorio")
    @JsonProperty("titulo")
    private String titulo;

    @NotBlank(message = "La descripcion es obligatoria")
    @JsonProperty("descripcion")
    private String descripcion;

    @JsonProperty("criterioDePertenencia")
    private CriterioInputDTO criterioDePertenencia;

    @JsonProperty("algoritmoConsenso")
    private String algoritmoConsenso;
}
