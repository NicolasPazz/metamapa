package ar.utn.ba.ddsi.dinamica.domain.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SolicitudModificarAdminInputDTO {
    @JsonProperty("id_solicitud")
    private Long idSolicitud;
    @JsonProperty("id_contribuyente")
    private Long idContribuyente;
    @JsonProperty("estado_solicitud")
    private String nuevoEstadoSolicitud;
    @JsonProperty ("sugerencia")
    private String nuevoSugerencia;
}
