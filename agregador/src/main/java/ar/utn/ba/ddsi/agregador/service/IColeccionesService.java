package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.dto.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.util.List;

public interface IColeccionesService {
    public List<ColeccionOutputDTO> buscarTodas();
    public List<Hecho> buscarHechosDeColeccion(String id);
    public ColeccionOutputDTO buscarColeccion(String id);
}