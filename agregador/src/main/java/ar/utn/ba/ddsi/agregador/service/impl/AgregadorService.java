package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.external.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.agregador.domain.repositorie.ISolicitudesRepository;
import ar.utn.ba.ddsi.agregador.domain.solicitud.SolicitudDeEliminacion;
import ar.utn.ba.ddsi.agregador.domain.solicitud.validacion.detector.DetectorDeSpam;
import ar.utn.ba.ddsi.agregador.service.IAgregadorService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgregadorService implements IAgregadorService {
    private final FuentesService fuentesService;
    private final IHechosRepository hechosRepository;
    private final ISolicitudesRepository solicitudesRepository;
    private final DetectorDeSpam detectorDeSpam;

    @Override
    public Mono<Void> actualizarHechos() {
        return this.fuentesService.getAllHechosNuevos()
            .doOnSubscribe(subscription -> log.info("[Agregador] Iniciando actualización de hechos..."))
            .doOnNext(lista -> log.info("[Agregador] Cantidad de hechos nuevos obtenidos: {}", lista.size()))
            .doOnError(error -> log.error("[Agregador] Error al obtener hechos nuevos: {}", error.getMessage(), error))
            .flatMap(lista -> {
                if (lista == null || lista.isEmpty()) {
                    log.info("[Agregador] No hay hechos nuevos para guardar.");
                    return Mono.empty();
                }

                List<Hecho> hechosNuevos = lista.stream()
                    .map(HechoDTO::mapToHecho)
                    .toList();

                hechosRepository.saveBatch(hechosNuevos);
                log.info("[Agregador] Se guardaron {} hechos nuevos.", hechosNuevos.size());

                return Mono.empty();
            });
    }
}
