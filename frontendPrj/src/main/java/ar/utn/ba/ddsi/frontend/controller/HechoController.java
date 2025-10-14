package ar.utn.ba.ddsi.frontend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hechos")
public class HechoController {
    private final HechoService hechoService;

    @GetMapping("/{idHecho}")
    verHechos();
}
