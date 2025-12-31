package ar.utn.ba.ddsi.frontend.models.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SolicitudDTO {
    private Long id;
    
    // El backend envía un objeto "hecho" con id, no "hecho_id"
    private HechoSimpleDTO hecho;
    
    @JsonProperty("texto_fundamentos")
    private String textoFundamentos;
    
    @JsonProperty("contribuyente_id")
    private Long idContribuyente;
    
    private LocalDateTime fecha;
    
    private String estado;
    
    @JsonProperty("es_spam")
    private Boolean spam;
    
    @JsonProperty("fuente_id")
    private Long fuenteId;
    
    @JsonProperty("id_origen")
    private Long idOrigen;
    
    // Getter para obtener el id del hecho desde el objeto anidado
    public Long getIdHecho() {
        return hecho != null ? hecho.getId() : null;
    }
}
