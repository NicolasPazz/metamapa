package ar.utn.ba.ddsi.servicio_fuente_estatica.controller;

import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.servicio_fuente_estatica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.servicio_fuente_estatica.service.ISolicitudesService;
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