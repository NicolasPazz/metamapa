package ar.utn.ba.ddsi.agregador.dto;

import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.TipoAlgoritmoConsenso;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class ColeccionDTO {
  private UUID handle;
  private String titulo;
  private String descripcion;
  private List<HechoDTO> hechos;

  @JsonProperty("criterio_de_pertenencia")
  private CriterioDTO criterioDePertenencia;

  @JsonProperty("algoritmo_consenso")
  private TipoAlgoritmoConsenso algoritmoConsenso;

  @JsonProperty("hechos_consensuados")
  private List<HechoDTO> hechosConsensuados;

  @JsonProperty("fecha_carga")
  private LocalDateTime fechaDeCarga;

  @JsonProperty("fuente_id")
  private Long fuenteId;

  @JsonProperty("id_origen")
  private UUID idOrigen;
}
