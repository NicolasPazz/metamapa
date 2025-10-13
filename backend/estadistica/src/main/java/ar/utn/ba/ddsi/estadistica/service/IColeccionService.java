package ar.utn.ba.ddsi.estadistica.service;

import ar.utn.ba.ddsi.estadistica.domain.Coleccion;
import java.util.List;

public interface IColeccionService {
  List<Coleccion> refrescarColecciones();
}
