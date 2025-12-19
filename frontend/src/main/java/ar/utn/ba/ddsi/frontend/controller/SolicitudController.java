package ar.utn.ba.ddsi.frontend.controller;

import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudDTO;
import ar.utn.ba.ddsi.frontend.models.dtos.SolicitudModificacionDTO;
import ar.utn.ba.ddsi.frontend.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

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
    @PostMapping("/modificar/{idSolicitud}")
    public ResponseEntity<?> solicitarModificacion(
            @PathVariable Long idSolicitud  ,
            @RequestBody @Valid SolicitudModificacionDTO dto) {



        //solicitudService.procesarSolicitudEdicion(idSolicitud, dto);

        //Prueba hardcodeada



        return ResponseEntity.ok("Solicitud recibida correctamente");
    }


    @GetMapping("/test1")
    public ResponseEntity<?> testearEdicion() {
        SolicitudModificacionDTO dtoPrueba = new SolicitudModificacionDTO();

        dtoPrueba.setIdContribuyente(1L);
        dtoPrueba.setNuevoTitulo("TEST: Título Modificado Hardcodeado");
        dtoPrueba.setNuevoDescripcion("Esta es una descripción de prueba generada manualmente en el backend para verificar el flujo de edición.");
        dtoPrueba.setNuevoNombreCategoria("Alau");
        dtoPrueba.setNuevoLatitud(-34.603722); //
        dtoPrueba.setNuevoLongitud(-58.381592);
        dtoPrueba.setNuevoFechaDeAcontecimiento(LocalDateTime.now().minusDays(1));

        solicitudService.procesarSolicitudEdicion(1L, dtoPrueba);

        return ResponseEntity.ok("Solicitud hardcodeada enviada con éxito");
    }
    
}



