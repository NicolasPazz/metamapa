package ar.utn.ba.ddsi.servicio_fuente_dinamica.service;

import ar.utn.ba.ddsi.servicio_fuente_dinamica.domain.hecho.Hecho;
import java.util.List;

public interface IFuenteService {
  public List<Hecho> importarHechos() throws Exception;
  public void abrirFuente() throws Exception;
  public void cerrarFuente();
}
