package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.domain.dto.external.HechosServiceDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.output.SolicitudOutputDTO;
import ar.utn.ba.ddsi.agregador.service.impl.HechosService;
import ar.utn.ba.ddsi.agregador.service.impl.SolicitudesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("api")
public class AgregadorController {
    private final SolicitudesService solicitudesService;
    private final HechosService hechosService;

    public AgregadorController(SolicitudesService solicitudesService, HechosService hechosService) {
        this.solicitudesService = solicitudesService;
        this.hechosService = hechosService;
    }

    @GetMapping("/hechos")
    public Mono<List<HechosServiceDTO>> getAllHechos(){
        return this.hechosService.getAllHechos();
    }

    @PostMapping("/solicitudes")
    public ResponseEntity<SolicitudOutputDTO> crear(@RequestBody SolicitudInputDTO solicitudInputDTO) {
        try {
            SolicitudOutputDTO respuesta = solicitudesService.crear(solicitudInputDTO);
            return ResponseEntity.ok(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
