package ar.utn.ba.ddsi.agregador.dto.mapper;

import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.Criterio;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.CriterioCombinado;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.CriterioFechas;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.CriterioFuente;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.CriterioIgualdad;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.CriterioLugar;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.CriterioOrigen;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.TipoDato;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.TipoFecha;
import ar.utn.ba.ddsi.agregador.domain.hecho.OrigenHecho;
import ar.utn.ba.ddsi.agregador.dto.CriterioCombinadoDTO;
import ar.utn.ba.ddsi.agregador.dto.CriterioDTO;
import ar.utn.ba.ddsi.agregador.dto.CriterioFechasDTO;
import ar.utn.ba.ddsi.agregador.dto.CriterioFuenteDTO;
import ar.utn.ba.ddsi.agregador.dto.CriterioIgualdadDTO;
import ar.utn.ba.ddsi.agregador.dto.CriterioLugarDTO;
import ar.utn.ba.ddsi.agregador.dto.CriterioOrigenDTO;
import ar.utn.ba.ddsi.agregador.dto.input.CriterioInputDTO;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CriterioMapper {

  public CriterioDTO toDTO(Criterio criterio) {
    if (criterio instanceof CriterioFechas) return toFechasDTO((CriterioFechas) criterio);
    if (criterio instanceof CriterioLugar) return toLugarDTO((CriterioLugar) criterio);
    if (criterio instanceof CriterioFuente) return toFuenteDTO((CriterioFuente) criterio);
    if (criterio instanceof CriterioIgualdad) return toIgualdadDTO((CriterioIgualdad) criterio);
    if (criterio instanceof CriterioCombinado) return toCombinadoDTO((CriterioCombinado) criterio);
    if (criterio instanceof CriterioOrigen) return toOrigenDTO((CriterioOrigen) criterio);
    throw new IllegalArgumentException("Tipo de criterio desconocido: " + criterio.getClass());
  }

  private CriterioFechasDTO toFechasDTO(CriterioFechas c) {
    CriterioFechasDTO dto = new CriterioFechasDTO();
    dto.setFechaInicial(c.getFechaInicial());
    dto.setFechaFinal(c.getFechaFinal());
    dto.setTipoFecha(c.getTipoFecha());
    return dto;
  }

  private CriterioLugarDTO toLugarDTO(CriterioLugar c) {
    CriterioLugarDTO dto = new CriterioLugarDTO();
    dto.setLatitud(c.getLat());
    dto.setLongitud(c.getLon());
    return dto;
  }

  private CriterioFuenteDTO toFuenteDTO(CriterioFuente c) {
    CriterioFuenteDTO dto = new CriterioFuenteDTO();
    dto.setFuentesExcluidas(c.getFuentesExcluidas());
    return dto;
  }

  private CriterioIgualdadDTO toIgualdadDTO(CriterioIgualdad c) {
    CriterioIgualdadDTO dto = new CriterioIgualdadDTO();
    dto.setCadenaAComparar(c.getCadenaAComparar());
    dto.setTipoDeDato(c.getTipoDeDato());
    return dto;
  }

  private CriterioCombinadoDTO toCombinadoDTO(CriterioCombinado c) {
    CriterioCombinadoDTO dto = new CriterioCombinadoDTO();
    List<CriterioDTO> criteriosDTO = c.getCriterios().stream()
        .map(this::toDTO)
        .collect(Collectors.toList());
    dto.setCriterios(criteriosDTO);
    return dto;
  }

  private CriterioOrigenDTO toOrigenDTO(CriterioOrigen c) {
    CriterioOrigenDTO dto = new CriterioOrigenDTO();
    dto.setOrigen(c.getOrigen());
    return dto;
  }

  public Criterio fromDTO(CriterioDTO dto) {
    if (dto instanceof CriterioFechasDTO) return fromFechasDTO((CriterioFechasDTO) dto);
    if (dto instanceof CriterioLugarDTO) return fromLugarDTO((CriterioLugarDTO) dto);
    //if (dto instanceof CriterioFuenteDTO) return fromFuenteDTO((CriterioFuenteDTO) dto);
    if (dto instanceof CriterioIgualdadDTO) return fromIgualdadDTO((CriterioIgualdadDTO) dto);
    if (dto instanceof CriterioCombinadoDTO) return fromCombinadoDTO((CriterioCombinadoDTO) dto);
    if (dto instanceof CriterioOrigenDTO) return fromOrigenDTO((CriterioOrigenDTO) dto);
    throw new IllegalArgumentException("Tipo de CriterioDTO desconocido: " + dto.getClass());
  }

  private CriterioFechas fromFechasDTO(CriterioFechasDTO dto) {
    return new CriterioFechas(dto.getFechaInicial(), dto.getFechaFinal(), dto.getTipoFecha());
  }

  private CriterioLugar fromLugarDTO(CriterioLugarDTO dto) {
    return new CriterioLugar(dto.getLatitud(), dto.getLongitud());
  }

  /*private CriterioFuente fromFuenteDTO(CriterioFuenteDTO dto) {
    return new CriterioFuente(dto.getFuentesExcluidas());
  }*/

  private CriterioIgualdad fromIgualdadDTO(CriterioIgualdadDTO dto) {
    return new CriterioIgualdad(dto.getCadenaAComparar(), dto.getTipoDeDato());
  }

  private CriterioCombinado fromCombinadoDTO(CriterioCombinadoDTO dto) {
    if (dto == null || dto.getCriterios() == null) {
      return new CriterioCombinado(List.of());
    }

    List<Criterio> criterios = dto.getCriterios().stream()
        .map(this::fromDTO)
        .collect(Collectors.toList());

    return new CriterioCombinado(criterios);
  }

  private CriterioOrigen fromOrigenDTO(CriterioOrigenDTO dto) {
    return new CriterioOrigen(dto.getOrigen());
  }

  public Criterio fromDTO(CriterioInputDTO dto) {
    if (dto == null || dto.getTipo() == null) {
      throw new IllegalArgumentException("CriterioInputDTO o tipo no puede ser null");
    }

    String tipo = dto.getTipo().toUpperCase();

    return switch (tipo) {
      case "FECHAS" -> new CriterioFechas(
          dto.getFechaInicial(),
          dto.getFechaFinal(),
          dto.getTipoFecha() != null ?
              TipoFecha.valueOf(dto.getTipoFecha().toUpperCase()) :
              null
      );
      case "LUGAR" -> new CriterioLugar(dto.getLat(), dto.getLng());
      case "IGUALDAD" -> new CriterioIgualdad(
          dto.getCadenaAComparar(),
          dto.getTipoDeDato() != null ?
              TipoDato.valueOf(dto.getTipoDeDato().toUpperCase()) :
              null
      );
      case "ORIGEN" -> new CriterioOrigen(
          dto.getOrigen() != null ?
              OrigenHecho.valueOf(dto.getOrigen().toUpperCase()) :
              null
      );
      default -> throw new IllegalArgumentException("Tipo de criterio desconocido: " + tipo);
    };
  }
}