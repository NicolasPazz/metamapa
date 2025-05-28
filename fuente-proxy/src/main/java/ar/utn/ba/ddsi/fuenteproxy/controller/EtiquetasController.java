package ar.utn.ba.ddsi.fuenteproxy.controller;

import ar.utn.ba.ddsi.fuenteproxy.domain.dto.output.EtiquetaOutputDTO;
import ar.utn.ba.ddsi.fuenteproxy.service.IEtiquetasService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/etiquetas")
@CrossOrigin(origins = "http://localhost:3000")
public class EtiquetasController {
  @Autowired
  private IEtiquetasService etiquetasService;

  @GetMapping
  public List<EtiquetaOutputDTO> buscarTodas() {
    return this.etiquetasService.buscarTodas();
  }
}