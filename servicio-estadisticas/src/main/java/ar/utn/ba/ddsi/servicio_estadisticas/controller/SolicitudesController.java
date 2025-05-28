package ar.utn.ba.ddsi.servicio_estadisticas.controller;

import ar.utn.ba.ddsi.servicio_estadisticas.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.servicio_estadisticas.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.servicio_estadisticas.service.ISolicitudesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/solicitudes")
@CrossOrigin(origins = "http://localhost:3000")
public class SolicitudesController {
  @Autowired
  private ISolicitudesService solicitudesService;

  @PostMapping
  public SolicitudOutputDTO crear(@RequestBody SolicitudInputDTO solicitudInputDTO) {
    return solicitudesService.crear(solicitudInputDTO);
  }
}