package ar.utn.ba.ddsi.estadisticas.service;

import ar.utn.ba.ddsi.estadisticas.domain.hecho.Hecho;
import java.util.List;

public interface IFuenteService {
  public List<Hecho> importarHechos() throws Exception;
  public void abrirFuente() throws Exception;
  public void cerrarFuente();
}
