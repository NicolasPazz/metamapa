package ar.utn.ba.ddsi.fuenteestatica.controller;

import ar.utn.ba.ddsi.fuenteestatica.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.fuenteestatica.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.fuenteestatica.service.ISolicitudesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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