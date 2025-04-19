package ar.edu.utn.frba.dds.model.domain.hecho;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Hecho {

  private DataHecho data;
  private UbicacionHecho ubicacion;
  private OrigenHecho origen;
  private EstadoHecho estado;

  public Hecho(DataHecho data, UbicacionHecho ubicacion, OrigenHecho origen, EstadoHecho estado) {
    this.data = data;
    this.ubicacion = ubicacion;
    this.origen = origen;
    this.estado = EstadoHecho.VISIBLE;
  }

  public boolean esDuplicadoDe(Hecho otroHecho) {
    // TODO
    return false;
  }

  public void marcarComoOculto() {
    this.estado = EstadoHecho.OCULTO;
  }
}
