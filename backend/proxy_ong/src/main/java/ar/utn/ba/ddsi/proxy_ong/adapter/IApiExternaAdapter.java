package ar.utn.ba.ddsi.proxy_ong.adapter;

import ar.utn.ba.ddsi.proxy_ong.domain.hecho.Hecho;
import java.util.List;

public interface IApiExternaAdapter {
  
  List<Hecho> obtenerTodosLosHechos();

  boolean verificarConexion();
}

