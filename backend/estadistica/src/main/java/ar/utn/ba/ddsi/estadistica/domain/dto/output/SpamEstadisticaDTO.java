package ar.utn.ba.ddsi.estadistica.domain.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpamEstadisticaDTO {
    private Long totalSolicitudes;
    private Long solicitudesSpam;
    private Long solicitudesValidas;
    private Double porcentajeSpam;
}
