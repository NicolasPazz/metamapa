package ar.utn.ba.ddsi.agregador.dto;

import ar.utn.ba.ddsi.agregador.domain.fuente.factory.TipoFuente;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FuenteDTO {
    private Long id;
    private String nombre;
    private String url;
    private TipoFuente tipo;

    @JsonProperty("activa")
    private Boolean activa;
}

