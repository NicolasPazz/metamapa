package ar.utn.ba.ddsi.agregador.dto.output;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.util.UUID;
import lombok.Data;

import java.util.List;

@Data
public class ColeccionOutputDTO {
    private UUID handle;
    private String titulo;
    private String descripcion;
    private List<Hecho> hechos;
}