package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.dtos.*;
import ar.utn.ba.ddsi.frontend.service.EstadisticasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class EstadisticasController {
    private final EstadisticasService estadisticasService;

    @GetMapping("/estadisticas")
    public String mostrarEstadisticas(Model model) {
        EstadisticasCompletasDTO estadisticas = estadisticasService.obtenerEstadisticas();
        List<ColeccionProvinciaDTO> coleccionesProvincia = estadisticas.getDistribucionGeograficaColecciones();
        List<CategoriaProvinciaDTO> categoriasProvincia = estadisticas.getConcentracionGeograficaCategorias();
        List<CategoriaHoraDTO> categoriasHora = estadisticas.getDistribucionTemporalCategorias();
        CategoriaDominanteDTO categoriaDominante = estadisticas.getCategoriaDominante();
        SpamDTO spam = estadisticas.getEstadisticaSpam();

        coleccionesProvincia.sort(Comparator.comparing(
                ColeccionProvinciaDTO::getColeccionHandle
        ));
        categoriasProvincia.sort(Comparator.comparing(
                CategoriaProvinciaDTO::getProvinciaNombre
        ));
        categoriasHora.sort(Comparator.comparing(
                CategoriaHoraDTO::getCategoriaNombre
        ));

        model.addAttribute("titulo", "Estadísticas");
        model.addAttribute("coleccionesProvincia", coleccionesProvincia);
        model.addAttribute("categoriasProvincia", categoriasProvincia);
        model.addAttribute("categoriasHora", categoriasHora);
        model.addAttribute("categoriaDominante", categoriaDominante);
        model.addAttribute("spam", spam);
        return "admin/estadisticas";
    }
}
