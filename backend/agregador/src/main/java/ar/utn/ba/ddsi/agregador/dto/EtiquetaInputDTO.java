package ar.utn.ba.ddsi.agregador.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EtiquetaInputDTO {
    @JsonProperty("nombre")
    private String nombreEtiqueta;
}
