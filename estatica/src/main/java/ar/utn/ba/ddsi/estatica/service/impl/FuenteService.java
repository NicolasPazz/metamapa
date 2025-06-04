package ar.utn.ba.ddsi.estatica.service.impl;

import ar.utn.ba.ddsi.estatica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.estatica.domain.hecho.Etiqueta;
import ar.utn.ba.ddsi.estatica.domain.hecho.Hecho;
import ar.utn.ba.ddsi.estatica.domain.repositories.IHechosRepository;
import ar.utn.ba.ddsi.estatica.service.IFuenteService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FuenteService implements IFuenteService {
  private final IHechosRepository hechosRepository;

  public FuenteService(IHechosRepository hechosRepository) {
    this.hechosRepository = hechosRepository;
  }

  @Override
  public List<HechoOutputDTO> getAllHechosVisibles(String categoria, String fecha_reporte_desde, String fecha_reporte_hasta, String fecha_acontecimiento_desde, String fecha_acontecimiento_hasta, String ubicacion) {
    return List.of();
  }

  @Override
  public List<HechoOutputDTO> getAllHechosNuevos() {
    List<Hecho> hechos = this.hechosRepository.findAll();
    return hechos
        .stream()
        .map(this::hechoOutputDTO)
        .toList();
  }

  private HechoOutputDTO hechoOutputDTO(Hecho hecho){
    var dto = new HechoOutputDTO();
    dto.setId(hecho.getId());
    dto.setOrigen(hecho.getOrigen().name());
    dto.setEstado(hecho.getEstado().name());
    if (hecho.getContribuyente() != null) {
      dto.setIdContribuyente(hecho.getContribuyente().getId());
    } else {
      dto.setIdContribuyente(null);
    }
    dto.setIdEtiquetas(hecho.getEtiquetas().stream()
        .map(Etiqueta::getId)
        .toList());
    dto.setTitulo(hecho.getTitulo());
    dto.setDescripcion(hecho.getDescripcion());
    dto.setCategoria(hecho.getNombreCategoria());
    dto.setLatitud(hecho.getLatitud());
    dto.setLongitud(hecho.getLongitud());
    dto.setFechaDeAcontecimiento(hecho.getFechaDeAcontecimiento().toString());
    dto.setFechaDeCarga(hecho.getFechaDeCarga().toString());
    if (!hecho.getTransiciones().isEmpty()) {
      dto.setUltimaModificacion(hecho.getTransiciones().get(hecho.getTransiciones().size() - 1).getFechaTransicion().toString());
    }
    dto.setMultimediaPaths(hecho.getMultimediaPaths());
    return dto;
  }
}