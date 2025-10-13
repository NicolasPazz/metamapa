package ar.utn.ba.ddsi.estadistica.domain.dto.input;

import lombok.Data;

import java.util.List;

@Data
public class ColeccionDTO {
    private String handle;
    private String titulo;
    private List<HechoDTO> hechos;
}
