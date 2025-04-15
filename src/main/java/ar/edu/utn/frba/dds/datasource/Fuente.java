package ar.edu.utn.frba.dds.datasource;

import ar.edu.utn.frba.dds.model.domain.hecho.Hecho;
import java.util.List;

public abstract class Fuente {
  private List<Hecho> hechos;

  public abstract List<Hecho> importarHechos();
  public abstract void abrirFuente();
  public abstract void leerHechos();
  public abstract void cerrarFuente();
}
