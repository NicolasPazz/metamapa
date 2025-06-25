package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.repositorie.IColeccionesRepository;
import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ColeccionesService {

    private final IColeccionesRepository coleccionesRepository;
    private final FuentesService fuentesService;

    public ResponseEntity<List<ColeccionDTO>> getAll() {
      List<ColeccionDTO> locales = coleccionesRepository.findAll().stream()
          .map(Coleccion::mapToDTO)
          .toList();

      List<ColeccionDTO> externas = Optional.ofNullable(
          fuentesService.getAllColecciones().getBody()
      ).orElse(List.of());

      List<ColeccionDTO> combinadas = new ArrayList<>();
      combinadas.addAll(locales);
      combinadas.addAll(externas);

      return ResponseEntity.of(Optional.of(combinadas));
    }

    public ResponseEntity<List<HechoDTO>> getByHandle(String handle) {
      // Buscar primero en las colecciones locales
      Coleccion local = coleccionesRepository.findByHandle(handle);
      if (local != null) {
        try {
          List<HechoDTO> hechosDTO = local.getHechos()
              .stream()
              .map(Hecho::mapToDTO)
              .toList();

          return ResponseEntity.ok(hechosDTO);
        } catch (Exception e) {
          return ResponseEntity.internalServerError().build();
        }
      }

      // Si no está en las locales, buscar en fuentes externas
      ResponseEntity<List<HechoDTO>> respuesta = fuentesService.getColeccionByHandle(handle);
      List<HechoDTO> externos = respuesta.getBody();

      if (externos != null) {
        return ResponseEntity.ok(externos);
      }

      return ResponseEntity.notFound().build();
    }
}
