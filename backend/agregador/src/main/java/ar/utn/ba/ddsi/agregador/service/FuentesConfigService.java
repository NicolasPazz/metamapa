package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.fuente.Fuente;
import ar.utn.ba.ddsi.agregador.dto.FuenteDTO;
import ar.utn.ba.ddsi.agregador.repositorie.IFuentesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuentesConfigService {

    private final IFuentesRepository fuentesRepository;

    public ResponseEntity<List<FuenteDTO>> getAllFuentes() {
        log.info("[FuentesConfigService] Obteniendo todas las fuentes");
        List<Fuente> fuentes = fuentesRepository.findAll();
        List<FuenteDTO> dtos = fuentes.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    public ResponseEntity<List<FuenteDTO>> getFuentesActivas() {
        log.info("[FuentesConfigService] Obteniendo fuentes activas");
        List<Fuente> fuentes = fuentesRepository.findByActivaTrue();
        List<FuenteDTO> dtos = fuentes.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private FuenteDTO toDTO(Fuente fuente) {
        return FuenteDTO.builder()
            .id(fuente.getId())
            .nombre(fuente.getNombre())
            .url(fuente.getUrl())
            .tipo(fuente.getTipo())
            .activa(fuente.getActiva())
            .build();
    }
}

