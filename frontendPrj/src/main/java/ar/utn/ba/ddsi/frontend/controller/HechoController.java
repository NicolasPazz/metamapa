package ar.utn.ba.ddsi.frontend.controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import ar.utn.ba.ddsi.frontend.models.dtos.HechoDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.frontend.service.HechoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/hechos")
public class HechoController {
    private final HechoService hechoService;

    @GetMapping
    public String listarHechos(Model model) {
        List<HechoDTO> hechos = hechoService.getAllHechos();
        model.addAttribute("hechos", hechos);
        model.addAttribute("titulo", "Hechos");
        model.addAttribute("solicitud", new SolicitudDTO());

        return "hechos/hechosencoleccion";
    }

    @GetMapping("/mapa")
    public String hechosMapa(Model model) throws JsonProcessingException {
        List<HechoDTO> hechos = hechoService.getAllHechos();
        model.addAttribute("hechos", hechos);
        model.addAttribute("titulo", "Mapa");
        return "mapa/mapa";
    }
}
