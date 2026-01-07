package ar.utn.ba.ddsi.frontend.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class ColeccionDTO {
  public String handle;
  public String titulo;
  public String descripcion;
  @JsonProperty("fecha_carga")
  public LocalDateTime fechaDeCarga;
  public List<HechoDTO> hechos;

  @JsonProperty("criterios")
  private List<CriterioInputDTO> criteriosDePertenencia;
  @JsonProperty("algoritmo_consenso")
  private String algoritmoConsenso;
  @JsonProperty("hechos_consensuados")
  private List<HechoDTO> hechosConsensuados;
  @JsonProperty("fuente_id")
  private Long fuenteId;
  @JsonProperty("id_origen")
  private UUID idOrigen;
  
  @JsonProperty("cantidad_hechos")
  public Integer cantidadHechos;
}
