package ar.utn.ba.ddsi.agregador.domain.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class EtiquetaInputDTO {
    @JsonProperty("id_hecho")
    private Long idHecho;
    @JsonProperty("nombre_etiqueta")
    private String nombreEtiqueta;
}
