package ar.utn.ba.ddsi.dinamica.service;

import ar.utn.ba.ddsi.dinamica.domain.hecho.Hecho;
import java.util.List;

public abstract class Fuente {

  public abstract List<Hecho> importarHechos() throws Exception;
  public abstract void abrirFuente() throws Exception;
  public abstract void cerrarFuente();
}
