package model.domain.user;

import model.domain.interfaces.Contribuyente;
import model.domain.request.SolicitudDeEliminacion;
import ar.edu.utn.frba.dds.datasource.dynamic.Hecho;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter

public class ContribuyenteAnonimo implements Contribuyente {

  public void subir(Hecho hecho){
    //TODO
  }

  public SolicitudDeEliminacion solicitarEliminacion(Hecho hecho, String fundamentos){
    //TODO
  }

}


