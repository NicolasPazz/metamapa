package ar.utn.ba.ddsi.dinamica.domain.dto.input;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class SolicitudModificarUserInputDTO {
  @NotNull(message = "El ID del contribuyente es obligatorio para modificar")
  @JsonProperty("contribuyente_id")
  private Long idContribuyente;

  @JsonProperty("titulo")
  private String nuevoTitulo;

  @JsonProperty("descripcion")
  private String nuevoDescripcion;

  @JsonProperty("categoria")
  private String nuevoNombreCategoria;

  @DecimalMin(value = "-90.0", message = "La latitud debe estar entre -90 y 90")
  @DecimalMax(value = "90.0", message = "La latitud debe estar entre -90 y 90")
  @JsonProperty("latitud")
  private Double nuevoLatitud;

  @DecimalMin(value = "-180.0", message = "La longitud debe estar entre -180 y 180")
  @DecimalMax(value = "180.0", message = "La longitud debe estar entre -180 y 180")
  @JsonProperty("longitud")
  private Double nuevoLongitud;

  @JsonProperty("fecha_acontecimiento")
  private LocalDateTime nuevoFechaDeAcontecimiento;

  @Valid
  @JsonProperty("multimedia")
  private List<ContenidoMultimediaInputDTO> nuevoContenidoMultimedia;
}
