package ar.utn.ba.ddsi.agregador.dto;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.TipoDato;
import lombok.Data;

@Data
public class CriterioIgualdadDTO implements CriterioDTO {
  private String cadenaAComparar;
  private TipoDato tipoDeDato;
}