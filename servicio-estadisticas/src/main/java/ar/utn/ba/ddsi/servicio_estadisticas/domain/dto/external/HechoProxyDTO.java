package ar.utn.ba.ddsi.servicio_estadisticas.domain.dto.external;

import lombok.Data;

@Data
public class HechoProxyDTO {
  private Long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private String fecha_hecho;
  private String created_at;
  private String updated_at;
}
