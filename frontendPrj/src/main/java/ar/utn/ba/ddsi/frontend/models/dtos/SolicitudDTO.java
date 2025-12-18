package ar.utn.ba.ddsi.frontend.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SolicitudDTO {
    private Long id;
    @JsonProperty("hecho_id")
    private Long idHecho;
    @JsonProperty("texto_fundamentos")
    private String textoFundamentos;
    @JsonProperty("contribuyente_id")
    private Long idContribuyente;
}
