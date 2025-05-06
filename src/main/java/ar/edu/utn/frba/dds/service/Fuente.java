package ar.edu.utn.frba.dds.service;

import ar.edu.utn.frba.dds.domain.hecho.Hecho;
import java.util.List;

public abstract class Fuente {

  public abstract List<Hecho> importarHechos() throws Exception;
  public abstract void abrirFuente() throws Exception;
  public abstract void cerrarFuente();
}
