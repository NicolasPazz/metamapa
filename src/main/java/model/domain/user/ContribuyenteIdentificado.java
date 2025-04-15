package model.domain.user;

import ar.edu.utn.frba.dds.datasource.dynamic.Hecho;
import lombok.Getter;
import lombok.Setter;
import model.domain.interfaces.Contribuyente;
import model.domain.request.SolicitudDeEliminacion;

@Getter
@Setter

public class ContribuyenteIdentificado implements Contribuyente {

  private String nombre;
  private String apellido;
  private String edad;

  public ContribuyenteIdentificado(String nombre, String apellido, String edad) {
      this.nombre = nombre;
      this.apellido = apellido;
      this.edad = edad;
  }

  public void subir(Hecho hecho) {
    //TODO
  }

  public SolicitudDeEliminacion solicitarEliminacion(Hecho hecho, String fundamentos) {
    //TODO
  }

}


