package ar.utn.frba.dds.metamapa.service.impl;

import ar.utn.frba.dds.metamapa.domain.dto.output.EtiquetaOutputDTO;
import ar.utn.frba.dds.metamapa.domain.hecho.Etiqueta;
import ar.utn.frba.dds.metamapa.domain.repositorie.IEtiquetasRepository;
import ar.utn.frba.dds.metamapa.service.IEtiquetasService;
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
