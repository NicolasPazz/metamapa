package ar.utn.ba.ddsi.estadistica.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Hecho {
  private String categoria;
  private ProvinciaEnum provincia;
  private LocalDateTime fechaDeCarga;
}
