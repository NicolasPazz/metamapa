package ar.utn.ba.ddsi.agregador.domain.dto;

import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.TipoAlgoritmoConsenso;
import java.util.UUID;
import lombok.Data;
import java.util.List;

@Data
public class ColeccionDTO {
  private UUID handle;
  private String titulo;
  private String descripcion;
  private List<HechoDTO> hechos;
  private CriterioDTO criterioDePertenencia;
  private TipoAlgoritmoConsenso algoritmoConsenso;
  private List<HechoDTO> hechosConsensuados;
}
