package ar.utn.ba.ddsi.proxy_metamapa.domain.dto.input;

import java.util.List;
import lombok.Data;

@Data
public class HechoInputDTO {
  // TODO: eliminar lo que no sea necesario
  private Long idContribuyente;
  private List<Long> idEtiquetas;
  private String titulo;
  private String descripcion;
  private Long idCategoria;
  private Double latitud;
  private Double longitud;
  private String fechaDeAcontecimiento;
  private String fechaDeCarga;
  private List<Long> idMultimedia;
}
