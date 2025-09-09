package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class AgregadorService implements IAgregadorService {
    private final FuentesService fuentesService;
    private final IHechosRepository hechosRepository;

    @Override
    public void actualizarHechos() {
      try {
        log.info("[Agregador] Iniciando actualización de hechos...");

        List<HechoDTO> nuevosDTOs = Optional.ofNullable(
            fuentesService.getAllHechosNuevos().getBody()
        ).orElse(List.of());

        log.info("[Agregador] Cantidad de hechos nuevos obtenidos: {}", nuevosDTOs.size());

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
