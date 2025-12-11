package ar.utn.ba.ddsi.agregador.dto.mapper;

import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.agregador.dto.ColeccionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper encargado de la conversión entre Coleccion y ColeccionDTO.
 */
@Component
@RequiredArgsConstructor
public class ColeccionMapper {

    private final HechoMapper hechoMapper;

    /**
     * Convierte una entidad Coleccion a su representación DTO.
     *
     * @param coleccion Entidad Coleccion a convertir
     * @return ColeccionDTO con todos los campos mapeados, o null si la colección es null
     */
    public ColeccionDTO toDTO(Coleccion coleccion) {
        if (coleccion == null) return null;

        ColeccionDTO dto = new ColeccionDTO();
        dto.setHandle(coleccion.getHandle());
        dto.setTitulo(coleccion.getTitulo());
        dto.setDescripcion(coleccion.getDescripcion());
        dto.setFechaDeCarga(coleccion.getFechaDeCarga());
        dto.setFuenteId(coleccion.getFuenteId());
        dto.setIdOrigen(coleccion.getIdOrigen());

        if (coleccion.getHechos() != null) {
            dto.setHechos(hechoMapper.toDTO(coleccion.getHechos()));
        }

        if (coleccion.getHechosConsensuados() != null) {
            dto.setHechosConsensuados(hechoMapper.toDTO(coleccion.getHechosConsensuados()));
        }

        if (coleccion.getAlgoritmoConsenso() != null) {
            dto.setAlgoritmoConsenso(coleccion.getAlgoritmoConsenso().getNombre());
        }

        return dto;
    }
}

