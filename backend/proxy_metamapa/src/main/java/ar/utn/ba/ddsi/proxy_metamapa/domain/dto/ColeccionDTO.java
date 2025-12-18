package ar.utn.ba.ddsi.proxy_metamapa.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColeccionDTO {
  @JsonProperty("handle")
  private UUID handle;

  @JsonProperty("titulo")
  private String titulo;

  @JsonProperty("descripcion")
  private String descripcion;

  @JsonProperty("fecha_carga")
  private LocalDateTime fechaDeCarga;

  @JsonProperty("hechos")
  private List<HechoDTO> hechos;

  @JsonProperty("hechos_consensuados")
  private List<HechoDTO> hechosConsensuados;
}

