package ar.utn.ba.ddsi.estadistica.domain.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaEstadisticaDTO {
    private String categoria;
    private Long cantidadHechos;
}
