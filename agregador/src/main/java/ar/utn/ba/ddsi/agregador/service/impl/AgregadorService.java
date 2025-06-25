package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.external.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.agregador.domain.repositorie.ISolicitudesRepository;
import ar.utn.ba.ddsi.agregador.domain.solicitud.validacion.detector.DetectorDeSpam;
import ar.utn.ba.ddsi.agregador.service.IAgregadorService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgregadorService implements IAgregadorService {
    private final FuentesService fuentesService;
    private final IHechosRepository hechosRepository;
    private final ISolicitudesRepository solicitudesRepository;
    private final DetectorDeSpam detectorDeSpam;

    @Override
    public void actualizarHechos() {
      try {
        log.info("[Agregador] Iniciando actualización de hechos...");

        List<HechoDTO> nuevosDTOs = Optional.ofNullable(
            fuentesService.getAllHechosNuevos().getBody()
        ).orElse(List.of());

        log.info("[Agregador] Cantidad de hechos nuevos obtenidos: {}", nuevosDTOs.size());

        if (nuevosDTOs.isEmpty()) {
          log.info("[Agregador] No hay hechos nuevos para guardar.");
        }

        List<Hecho> hechosNuevos = nuevosDTOs.stream()
            .map(HechoDTO::mapToHecho)
            .toList();

        hechosRepository.saveBatch(hechosNuevos);
        log.info("[Agregador] Se guardaron {} hechos nuevos.", hechosNuevos.size());
      } catch (Exception e) {
        log.error("[Agregador] Error al actualizar hechos: {}", e.getMessage(), e);
      }
    }
}
