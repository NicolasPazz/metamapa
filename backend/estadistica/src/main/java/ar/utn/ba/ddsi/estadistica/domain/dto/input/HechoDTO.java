package ar.utn.ba.ddsi.estadistica.domain.dto.input;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HechoDTO {
    private String categoria;
    private Double latitud;
    private Double longitud;
    private LocalDateTime fechaDeCarga;
}
