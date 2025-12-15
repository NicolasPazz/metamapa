package ar.utn.ba.ddsi.agregador.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProvinciaEstadisticaDTO {
    private String provincia;
    private Long cantidad;
}

