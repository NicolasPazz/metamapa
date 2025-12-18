package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudModificarHechoDTO;
import ar.utn.ba.ddsi.frontend.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/solicitudes")

public class SolicitudController {
    private final SolicitudService solicitudService;
    public String crearSolicitud(@ModelAttribute("solicitud") SolicitudDTO solicitudDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        try {
            solicitudService.crearSolicitud(solicitudDTO);
            return "redirect:/hechos";
        } catch (Exception e) {
            return "redirect:/hechos";
        }


    }
    @PutMapping("/modificar/{idHecho}")
    public ResponseEntity<?> solicitarModificacion(
            @PathVariable Long idHecho,
            @RequestBody @Valid SolicitudModificarHechoDTO dto) {


        //solicitudService.procesarSolicitudEdicion(idHecho, dto);

        return ResponseEntity.ok("Solicitud recibida correctamente");
    }
}

    /*
    @PostMapping("/edicion") // La URL completa será: POST /api/solicitudes/edicion
    public ResponseEntity<?> crearSolicitudEdicion(@RequestBody SolicitudEdicionInputDTO input) {

        log.info("Nueva solicitud de edición para hecho ID: {}", input.getHechoId());

        // Llamamos al servicio
        solicitudesService.procesarSolicitudEdicion(input);

        return ResponseEntity.ok("Solicitud de edición creada correctamente");
    }*/

