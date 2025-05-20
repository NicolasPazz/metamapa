package ar.utn.frba.dds.metamapa.service;

import ar.utn.frba.dds.metamapa.domain.hecho.Hecho;
import java.util.List;

public interface IFuenteService {
  public List<Hecho> importarHechos() throws Exception;
  public void abrirFuente() throws Exception;
  public void cerrarFuente();
}
