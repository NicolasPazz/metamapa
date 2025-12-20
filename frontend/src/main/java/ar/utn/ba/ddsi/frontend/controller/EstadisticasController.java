package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.dtos.EstadisticasCompletasDTO;
import ar.utn.ba.ddsi.frontend.service.EstadisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EstadisticasController {
    private final EstadisticasService estadisticasService;

    @GetMapping("/estadisticas")
    public String mostrarEstadisticas(Model model) {
        List<EstadisticasCompletasDTO> estadisticas = estadisticasService.obtenerEstadisticas();

        model.addAttribute("titulo", "Estadísticas");
        model.addAttribute("estadisticas", estadisticas);
        return "admin/estadisticas";
    }
}
