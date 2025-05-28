package ar.utn.ba.ddsi.agregador.domain.dto.output;

import java.util.List;
import lombok.Data;

@Data
public class HechoOutputDTO {
  // TODO: eliminar lo que no sea necesario
  private Long id;
  private String origen;
  private String estado;
  private Long idContribuyente;
  private List<Long> idEtiquetas;
  private String titulo;
  private String descripcion;
  private Long idCategoria;
  private Double latitud;
  private Double longitud;
  private String fechaDeAcontecimiento;
  private String fechaDeCarga;
  private List<String> multimediaPaths;
}
