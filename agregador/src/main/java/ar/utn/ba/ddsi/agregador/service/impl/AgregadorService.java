package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.external.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.agregador.domain.repositorie.impl.ColeccionesRepository;
import ar.utn.ba.ddsi.agregador.service.IAgregadorService;
import ar.utn.ba.ddsi.agregador.service.IColeccionesService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgregadorService implements IAgregadorService {
    private final FuentesService fuentesService;
    private final IHechosRepository hechosRepository;

    @Override
    public void actualizarHechos() {
        List<HechoDTO> hechosDTO = Optional.ofNullable(this.fuentesService.getAllHechosNuevos().block())
            .orElse(List.of());

        if (hechosDTO.isEmpty()) return;

        List<Hecho> hechosNuevos = hechosDTO.stream()
            .map(HechoDTO::mapToHecho)
            .collect(Collectors.toList());

        hechosRepository.saveBatch(hechosNuevos);
    }
}
