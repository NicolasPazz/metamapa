package ar.utn.ba.ddsi.agregador.domain.dto;

import lombok.Data;

@Data
public class CriterioLugarDTO implements CriterioDTO {
  private Double lat;
  private Double lon;
}
