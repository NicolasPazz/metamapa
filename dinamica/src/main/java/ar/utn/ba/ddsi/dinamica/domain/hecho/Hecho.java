package ar.utn.ba.ddsi.dinamica.domain.hecho;

import java.time.LocalDate;
import ar.utn.ba.ddsi.dinamica.domain.user.Contribuyente;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hecho {
  private Long id;
  private Long idSolicitud;
  private DataHecho data;
  private OrigenHecho origen;
  private Long idContribuyente;
  private List<String> multimediaPaths = new ArrayList<>();

  public boolean esDuplicadoDe(Hecho otroHecho) {
    return this.getTitulo().equalsIgnoreCase(otroHecho.getTitulo());
  }


  public String getTitulo() {
    return data.getTitulo();
  }

  public String getDescripcion() {
    return data.getDescripcion();
  }

  public String getNombreCategoria() { return data.getNombreCategoria(); }

  public Double getLatitud() {
    return data.getLatitud();
  }

  public Double getLongitud() {
    return data.getLongitud();
  }

  public LocalDate getFechaDeAcontecimiento() {
    return data.getFechaDeAcontecimiento();
  }

  public LocalDate getFechaDeCarga() {
    return data.getFechaDeCarga();
  }
}