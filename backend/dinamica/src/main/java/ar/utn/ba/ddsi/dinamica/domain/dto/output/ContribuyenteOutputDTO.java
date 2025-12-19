package ar.utn.ba.ddsi.dinamica.domain.dto.output;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ContribuyenteOutputDTO {
    @JsonProperty("contribuyente_id")
    private Long idContribuyente;
    private String nombre;
    private String apellido;
}
