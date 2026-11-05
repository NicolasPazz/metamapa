package ar.utn.ba.ddsi.dinamica.domain.multimedia;

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
 * Archivo multimedia subido con una solicitud. Se guarda en la base de datos para que
 * no dependa del disco del servidor (que puede ser efimero) y para que otros servicios
 * puedan obtenerlo por HTTP.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "archivo_multimedia")
public class ArchivoMultimedia {

  /** Nombre generado: UUID + extension. */
  @Id
  @Column(length = 64)
  private String nombre;

  @Column(nullable = false)
  private Long tamanio;

  @Column(nullable = false)
  private LocalDateTime fechaSubida;

  @Lob
  @Column(nullable = false, columnDefinition = "LONGBLOB")
  private byte[] contenido;
}
