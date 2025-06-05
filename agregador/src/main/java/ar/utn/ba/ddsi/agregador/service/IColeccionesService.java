package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterioDePertenencia.Criterio;
import ar.utn.ba.ddsi.agregador.domain.dto.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.util.List;

public interface IColeccionesService {
    public List<ColeccionOutputDTO> buscarTodas();
    public List<Hecho> buscarHechosDeColeccion(String id) throws Exception;
    public ColeccionOutputDTO buscarColeccion(String id);
    public void actualizarColecciones(List<Hecho> hechos);
}