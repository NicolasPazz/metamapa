package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IHechosRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HechosService{

    @Autowired
    private final IHechosRepository hechosRepository;
    @Autowired
    private final FuentesService fuentesService;

    public ResponseEntity<List<HechoDTO>> getAll(
        String categoria,
        LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
        LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
        String ubicacion) {

        ResponseEntity<List<HechoDTO>> respuestaExternos  = fuentesService.getAllHechosVisibles(
            categoria,
            fecha_reporte_desde, fecha_reporte_hasta,
            fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
            ubicacion);

        List<HechoDTO> externos = Optional.ofNullable(respuestaExternos.getBody()).orElse(List.of());

        List<HechoDTO> locales = hechosRepository.findByFilters(
            categoria,
            fecha_reporte_desde, fecha_reporte_hasta,
            fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
            ubicacion
            ).stream()
             .map(Hecho::mapToDTO)
             .toList();

        List<HechoDTO> combinados = new ArrayList<>();
        combinados.addAll(locales);
        combinados.addAll(externos);

        return ResponseEntity.of(Optional.of(combinados));
    }
    public HechoDTO crearHecho(HechoDTO hechoInputDTO){
        Hecho hechoNuevo = hechoInputDTO.mapToHecho();
        hechosRepository.save(hechoNuevo);
        return hechoInputDTO;
    }
}