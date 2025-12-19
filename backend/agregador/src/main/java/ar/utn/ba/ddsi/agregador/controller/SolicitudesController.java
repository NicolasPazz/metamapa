package ar.utn.ba.ddsi.agregador.controller;

import ar.utn.ba.ddsi.agregador.dto.SolicitudEliminacionInputDTO;
import ar.utn.ba.ddsi.agregador.service.SolicitudesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SolicitudesController {

    private final SolicitudesService solicitudesService;

    @PostMapping
    public ResponseEntity<?> crearSolicitudEliminacion(@RequestBody SolicitudEliminacionInputDTO solicitudInputDTO) {
        log.info("[SolicitudesController] POST /api/solicitudes - id: {}, hechoId: {}, textoFundamentos: {}, fecha: {}, estado: {}, contribuyenteId: {}, fuenteId: {}, idOrigen: {}",
                solicitudInputDTO.getId(), solicitudInputDTO.getHechoId(), solicitudInputDTO.getTextoFundamentos(),
                solicitudInputDTO.getFecha(), solicitudInputDTO.getEstado(), solicitudInputDTO.getContribuyenteId(),
                solicitudInputDTO.getFuenteId(), solicitudInputDTO.getIdOrigen());
        return solicitudesService.crearSolicitud(solicitudInputDTO);
    }
}

