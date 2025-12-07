package ar.utn.ba.ddsi.agregador.domain.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ActualizarColeccionInputDTO {
    @JsonProperty("titulo")
    private String titulo;
    @JsonProperty("descripcion")
    private String descripcion;
    @JsonProperty("criterioDePertenencia")
    private CriterioInputDTO criterioDePertenencia;
    @JsonProperty("algoritmoConsenso")
    private String algoritmoConsenso;
}
