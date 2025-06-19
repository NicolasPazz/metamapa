package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.dto.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.util.List;

public interface IColeccionesService {
    public List<ColeccionOutputDTO> getAll();
    public List<Hecho> getByHandle(String handle) throws Exception;
    public ColeccionOutputDTO buscarColeccion(String handle);
    public void actualizarColecciones(List<Hecho> hechos);
}