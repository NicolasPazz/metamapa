package ar.utn.ba.ddsi.agregador.dto;

import lombok.Data;

@Data
public class CriterioLugarDTO implements CriterioDTO {
  private Double lat;
  private Double lon;
}
