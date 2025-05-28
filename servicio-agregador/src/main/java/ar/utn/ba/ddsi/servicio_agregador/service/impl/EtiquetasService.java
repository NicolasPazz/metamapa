package ar.utn.ba.ddsi.servicio_agregador.service.impl;

import ar.utn.ba.ddsi.servicio_agregador.domain.dto.output.EtiquetaOutputDTO;
import ar.utn.ba.ddsi.servicio_agregador.domain.hecho.Etiqueta;
import ar.utn.ba.ddsi.servicio_agregador.domain.repositorie.IEtiquetasRepository;
import ar.utn.ba.ddsi.servicio_agregador.service.IEtiquetasService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EtiquetasService implements IEtiquetasService {
  @Autowired
  private IEtiquetasRepository etiquetasRepository;

  @Override
  public List<EtiquetaOutputDTO> buscarTodas() {
    return this.etiquetasRepository
        .findAll()
        .stream()
        .map(this::etiquetaOutputDTO)
        .toList();
  }

  private EtiquetaOutputDTO etiquetaOutputDTO(Etiqueta etiqueta) {
    var dto = new EtiquetaOutputDTO();
    dto.setId(etiqueta.getId());
    dto.setNombre(etiqueta.getNombre());
    return dto;
  }
}
