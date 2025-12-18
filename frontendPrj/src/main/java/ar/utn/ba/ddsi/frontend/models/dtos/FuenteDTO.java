package ar.utn.ba.ddsi.frontend.models.dtos;

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
}
