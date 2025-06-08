package ar.utn.ba.ddsi.domain.dto.external;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HechoProxyDTO {
  private Long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private Double latitud;
  private Double longitud;
  private LocalDate fecha_hecho;
  private LocalDateTime created_at;
  private LocalDateTime updated_at;
}
