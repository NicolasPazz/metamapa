package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.domain.dto.external.HechosServiceDTO;
import ar.utn.ba.ddsi.agregador.service.impl.HechosService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/hechos")
public class AgregadorController {

    private final HechosService hechosService;


    public AgregadorController(HechosService hechosService) {
        this.hechosService = hechosService;
    }

    @GetMapping
    public Mono<List<HechosServiceDTO>> getAllHechos(){
        return this.hechosService.getAllHechos();
    }
}
