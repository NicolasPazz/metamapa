package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterioDePertenencia.Criterio;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IColeccionesRepository;
import ar.utn.ba.ddsi.agregador.service.IColeccionesService;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ar.utn.ba.ddsi.agregador.domain.dto.output.ColeccionOutputDTO;

import java.util.Collection;
import java.util.List;

@Service
public class ColeccionesService implements IColeccionesService {
    private IColeccionesRepository coleccionRepository;

    @SneakyThrows
    @Override
    public List<ColeccionOutputDTO> buscarTodas() {
        return coleccionRepository.findAll().stream().map(this::coleccionOutputDTO).toList();
    }

    @Override
    public Criterio buscarCriterioDeColeccion(String id) throws Exception {
        return coleccionRepository.findById(id).getCriterioDePertenencia();
    }

    @Override
    public ColeccionOutputDTO buscarColeccion(String id) {
        Coleccion coleccion = coleccionRepository.findById(id);
        return coleccionOutputDTO(coleccion);
    }

    private ColeccionOutputDTO coleccionOutputDTO(Coleccion coleccion) {
        ColeccionOutputDTO dto = new ColeccionOutputDTO();

        dto.setHandler(coleccion.getHandle());
        dto.setTitulo(coleccion.getTitulo());
        //dto.setHechos(coleccion.getHechos());
        dto.setDescripcion(coleccion.getDescripcion());
        return dto;
    }
}
