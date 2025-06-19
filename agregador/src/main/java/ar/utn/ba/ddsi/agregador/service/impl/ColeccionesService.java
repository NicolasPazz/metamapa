package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IColeccionesRepository;
import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColeccionesService {

    private final IColeccionesRepository coleccionesRepository;
    private final FuentesService fuentesService;

    public Mono<List<ColeccionDTO>> getAll() {
        Mono<List<ColeccionDTO>> coleccionesLocales = Mono.just(
            coleccionesRepository.findAll().stream()
                .map(Coleccion::mapToDTO)
                .toList()
        );

        Mono<List<ColeccionDTO>> coleccionesExternas = fuentesService.getAllColecciones();

        return Mono.zip(coleccionesLocales, coleccionesExternas)
            .map(tuple -> {
                List<ColeccionDTO> combinadas = new ArrayList<>();
                combinadas.addAll(tuple.getT1());
                combinadas.addAll(tuple.getT2());
                return combinadas;
            });
    }

    public Mono<List<HechoDTO>> getByHandle(String handle) {
        Coleccion local = coleccionesRepository.findByHandle(handle);
        if (local != null) {
            try {
                List<HechoDTO> hechosDTO = local.getHechos()
                    .stream()
                    .map(Hecho::mapToDTO)
                    .toList();
                return Mono.just(hechosDTO);
            } catch (Exception e) {
                return Mono.error(e);
            }
        }

        return fuentesService.getColeccionByHandle(handle)
            .flatMap(coleccionDTO -> {
                List<HechoDTO> hechos = coleccionDTO.getHechos();
                return Mono.just(hechos);
            })
            .switchIfEmpty(Mono.error(new IllegalArgumentException("No se encontró ninguna colección con el handle: " + handle)));
    }

}
