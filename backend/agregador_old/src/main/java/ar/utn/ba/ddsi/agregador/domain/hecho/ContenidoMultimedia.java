package ar.utn.ba.ddsi.agregador.domain.hecho;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ContenidoMultimedia {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY)
  private Long id;
  @Column(name = "nombre")
  private String pathArchivo;
  @Enumerated(EnumType.ORDINAL)
  private TipoContenido tipoContenido;

  public ContenidoMultimedia(String pathArchivo, TipoContenido tipoContenido) {
    this.pathArchivo = pathArchivo;
    this.tipoContenido = tipoContenido;
  }
}
