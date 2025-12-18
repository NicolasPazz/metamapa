package ar.utn.ba.ddsi.agregador.domain.hecho.multimedia;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contenido_multimedia")
public class ContenidoMultimedia {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "path", nullable = false)
  private String pathArchivo;

  @Enumerated(EnumType.STRING)
  @Column(name="tipo_contenido", nullable = false)
  private TipoContenido tipoContenido;

  public ContenidoMultimedia(String pathArchivo, TipoContenido tipoContenido) {
    this.pathArchivo = pathArchivo;
    this.tipoContenido = tipoContenido;
  }
}
