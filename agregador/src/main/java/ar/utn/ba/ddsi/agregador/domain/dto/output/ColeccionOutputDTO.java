package ar.utn.ba.ddsi.agregador.domain.dto.output;

import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import lombok.Data;

import java.util.List;

@Data
public class ColeccionOutputDTO {
    private String handler;
    private String titulo;
    private String descripcion;
    private List<Hecho> hechos;
}