package ar.utn.ba.ddsi.estatica.domain.dto.output;

import lombok.Data;
import java.util.List;

@Data
public class HechoOutputDTO {
    private Long id;
    private String origen;
    private String estado;
    private Long idContribuyente;
    private List<Long> idEtiquetas;
    private String titulo;
    private String descripcion;
    private String categoria;
    private Double latitud;
    private Double longitud;
    private String fechaDeAcontecimiento;
    private String fechaDeCarga;
    private String ultimaModificacion;
    private List<String> multimediaPaths;
}