package ar.edu.utn.frba.dds.datasource;

import ar.edu.utn.frba.dds.datasource.dynamic.Hecho;
import java.util.List;

public abstract class FuenteDeDato {

  public abstract List<Hecho> importarHechos();

  public abstract void abrirFuente();

  public abstract void leerHechos();

  public abstract void cerrarFuente();

}
