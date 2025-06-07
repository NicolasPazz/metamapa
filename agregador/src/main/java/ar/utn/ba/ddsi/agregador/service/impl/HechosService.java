package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ContenidoMultimediaDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.ContribuyenteDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.TransicionEstadoHechoDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Etiqueta;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HechosService {

    private final IHechosRepository hechosRepository;
    private final FuentesService fuentesService;

    public Mono<List<HechoDTO>> getAll(String categoria,
                                       String fecha_reporte_desde,
                                       String fecha_reporte_hasta,
                                       String fecha_acontecimiento_desde,
                                       String fecha_acontecimiento_hasta,
                                       String ubicacion) {

        Mono<List<HechoDTO>> externos = fuentesService.getAllHechosVisibles(
            categoria,
            fecha_reporte_desde,
            fecha_reporte_hasta,
            fecha_acontecimiento_desde,
            fecha_acontecimiento_hasta,
            ubicacion
        );

        Mono<List<HechoDTO>> locales = Mono.fromSupplier(() ->
            hechosRepository.findAll().stream()
                .map(Hecho::mapToDTO)
                .collect(Collectors.toList())
        );

        return Mono.zip(externos, locales, (ext, loc) -> {
            List<HechoDTO> combinados = new ArrayList<>();
            combinados.addAll(loc);
            combinados.addAll(ext);
            return combinados;
        });
    }
}