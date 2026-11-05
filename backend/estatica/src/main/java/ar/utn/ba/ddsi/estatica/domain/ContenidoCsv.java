package ar.utn.ba.ddsi.estatica.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contenido de un CSV subido. Se guarda en la base de datos para que el archivo no se
 * pierda si el disco del servidor es efimero (ej. plan gratuito de Render).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contenido_csv")
public class ContenidoCsv {

  @Id
  private String nombre;

  @Column(nullable = false)
  private LocalDateTime fechaSubida;

  @Lob
  @Column(nullable = false, columnDefinition = "LONGBLOB")
  private byte[] contenido;
}
