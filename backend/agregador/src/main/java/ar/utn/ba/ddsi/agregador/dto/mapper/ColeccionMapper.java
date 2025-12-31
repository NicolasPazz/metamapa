package ar.utn.ba.ddsi.agregador.dto.mapper;

import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.agregador.dto.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.dto.CriterioDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ColeccionMapper {

    private final HechoMapper hechoMapper;
    private final CriterioMapper criterioMapper;

    public ColeccionDTO toDTO(Coleccion coleccion) {
        if (coleccion == null) return null;

        ColeccionDTO dto = new ColeccionDTO();
        dto.setHandle(coleccion.getHandle());
        dto.setTitulo(coleccion.getTitulo());
        dto.setDescripcion(coleccion.getDescripcion());
        dto.setFechaDeCarga(coleccion.getFechaDeCarga());

        if (coleccion.getHechos() != null) {
            dto.setHechos(hechoMapper.toDTO(coleccion.getHechos()));
            dto.setCantidadHechos(coleccion.getHechos().size());
        } else {
            dto.setCantidadHechos(0);
        }
        
        if (coleccion.getHechosConsensuados() != null) {
            dto.setHechosConsensuados(hechoMapper.toDTO(coleccion.getHechosConsensuados()));
        }
        
        if (coleccion.getAlgoritmoConsenso() != null) {
            dto.setAlgoritmoConsenso(coleccion.getAlgoritmoConsenso().getNombre());
        }

        if (coleccion.getCriteriosDePertenencia() != null && !coleccion.getCriteriosDePertenencia().isEmpty()) {
            List<CriterioDTO> criteriosDTO = coleccion.getCriteriosDePertenencia().stream()
                .map(criterioMapper::toDTO)
                .toList();
            dto.setCriteriosDePertenencia(criteriosDTO);
        }

        return dto;
    }

    public ColeccionDTO toDTOWithoutHechos(Coleccion coleccion) {
        if (coleccion == null) return null;

        ColeccionDTO dto = new ColeccionDTO();
        dto.setHandle(coleccion.getHandle());
        dto.setTitulo(coleccion.getTitulo());
        dto.setDescripcion(coleccion.getDescripcion());
        dto.setFechaDeCarga(coleccion.getFechaDeCarga());
        
        if (coleccion.getHechos() != null) {
            dto.setCantidadHechos(coleccion.getHechos().size());
        } else {
            dto.setCantidadHechos(0);
        }
        
        if (coleccion.getAlgoritmoConsenso() != null) {
            dto.setAlgoritmoConsenso(coleccion.getAlgoritmoConsenso().getNombre());
        }

        if (coleccion.getCriteriosDePertenencia() != null && !coleccion.getCriteriosDePertenencia().isEmpty()) {
            List<CriterioDTO> criteriosDTO = coleccion.getCriteriosDePertenencia().stream()
                .map(criterioMapper::toDTO)
                .toList();
            dto.setCriteriosDePertenencia(criteriosDTO);
        }

        return dto;
    }
}

