package ar.utn.ba.ddsi.agregador.domain.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class HechosServiceDTO {
    private Long id;
    private String origen;
    private String estado;
    private Long idContribuyente;
    private List<Long> idEtiquetas;
    private String titulo;
    private String descripcion;
    private Long idCategoria;
    private double latitud;
    private double longitud;
    private String fechaDeAcontecimiento;
    private String fechaDeCarga;
    private List<String> multimediaPaths;
}
