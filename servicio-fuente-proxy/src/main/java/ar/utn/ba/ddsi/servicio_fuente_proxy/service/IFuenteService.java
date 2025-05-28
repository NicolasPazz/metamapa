package ar.utn.ba.ddsi.servicio_fuente_proxy.service;

import ar.utn.ba.ddsi.servicio_fuente_proxy.domain.hecho.Hecho;
import java.util.List;

public interface IFuenteService {
  public List<Hecho> importarHechos() throws Exception;
  public void abrirFuente() throws Exception;
  public void cerrarFuente();
}
