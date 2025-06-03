package ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output;

import java.util.List;
import lombok.Data;

@Data
public class HechoOutputDTO {
  private Long id;
  private String origen;
  private String estado;
  private Long idContribuyente;
  private List<Long> idEtiquetas;
  private String titulo;
  private String descripcion;
  private Double latitud;
  private Double longitud;
  private String fechaDeAcontecimiento;
  private String fechaDeCarga;
  private String ultimaModificacion;
  private List<String> multimediaPaths;

  public HechoOutputDTO(
      Long id,
      String titulo,
      String descripcion,
      Double latitud,
      Double longitud,
      String fechaDeAcontecimiento,
      String fechaDeCarga,
      String ultimaModificacion
  ) {
    this.id = id;
    this.origen = "ONG";
    this.estado = "VISIBLE";
    this.idContribuyente = null;
    this.idEtiquetas = List.of();
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaDeAcontecimiento = fechaDeAcontecimiento;
    this.fechaDeCarga = fechaDeCarga;
    this.ultimaModificacion = ultimaModificacion;
    this.multimediaPaths = List.of();
  }
}
