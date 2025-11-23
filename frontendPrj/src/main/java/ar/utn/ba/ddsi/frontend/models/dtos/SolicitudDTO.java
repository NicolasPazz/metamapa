package ar.utn.ba.ddsi.frontend.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SolicitudDTO {
    private Long id;
    private Long idHecho;
    private String textoFundamentos;
    private Long idContribuyente;
}
