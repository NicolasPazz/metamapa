package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.domain.dto.output.ColeccionOutputDTO;
import ar.utn.ba.ddsi.agregador.service.impl.HechosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ar.utn.ba.ddsi.agregador.service.IColeccionesService;

import java.util.List;

@RestController
@RequestMapping("api/colecciones")
@CrossOrigin(origins = "http://localhost:3000")
public class ColeccionesController {
    @Autowired
    private IColeccionesService coleccionService;
    private HechosService hechosService;

    @GetMapping
    public List<ColeccionOutputDTO> buscarTodas() {
        return this.coleccionService.buscarTodas();
    }

    //@GetMapping("{id}/hechos")
    /*public List<HechoOutputDTO> buscarHechosDeColeccion(@PathVariable String handle) {
        return this.coleccionService.buscarHechosDeColeccion(handle).stream().map(this.hechosService::hechoOutputDTO).toList();
    }*/
}
