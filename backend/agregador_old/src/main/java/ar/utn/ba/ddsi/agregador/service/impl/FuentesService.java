package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.dto.external.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.domain.fuente.IFuente;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuentesService {

  private final List<IFuente> fuentes = new ArrayList<>();
  private final IdService idService;

  public void agregarFuente(IFuente fuente) {
    if (!fuentes.contains(fuente)) {
      fuentes.add(fuente);
    }
  }

  public ResponseEntity<List<HechoDTO>> getAllHechosVisibles(
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
      String ubicacion) {
    List<HechoDTO> resultados = new ArrayList<>();
    for (IFuente fuente : fuentes) {
      Long idFuente = fuente.getId();
      List<HechoDTO> hechos = Optional.ofNullable(
          fuente.getAllHechosVisibles(categoria,
              fecha_reporte_desde, fecha_reporte_hasta,
              fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
              ubicacion).getBody()
      ).orElse(List.of());
      // Setear ID unico
      hechos.forEach(h -> {
        if (h.getId() != null) {
          h.setId(idService.getId(idFuente, h.getId()));
        } else {
          log.warn("Hecho sin id recibido desde fuente {}", idFuente);
        }
      });
      resultados.addAll(hechos);
    }

    log.info("[FuentesService] Se obtuvieron {} hechos visibles combinados de todas las fuentes tiempo real.",
        resultados.size());

    return ResponseEntity.of(Optional.of(resultados));
  }




  public ResponseEntity<List<HechoDTO>> getAllHechosNuevos() {
    List<HechoDTO> resultados = new ArrayList<>();
    for (IFuente fuente : fuentes) {
      Long idFuente = fuente.getId();
      List<HechoDTO> hechos = Optional.ofNullable(
          fuente.getAllHechosNuevos().getBody()
      ).orElse(List.of());
      // Setear ID unico
      hechos.forEach(h -> {
        if (h.getId() != null) {
          //h.setId(idService.getId(idFuente, h.getId()));
        } else {
          log.warn("Hecho sin id recibido desde fuente {}", idFuente);
        }
      });
      resultados.addAll(hechos);
    }

    return ResponseEntity.of(Optional.of(resultados));
  }

  public ResponseEntity<List<ColeccionDTO>> getAllColecciones() {
    List<ColeccionDTO> resultados = new ArrayList<>();

    for (IFuente fuente : fuentes) {
      Long idFuente = fuente.getId();

      List<ColeccionDTO> colecciones = Optional.ofNullable(
          fuente.getAllColecciones().getBody()
      ).orElse(List.of());

      // Setear ID unico para cada hecho dentro de las colecciones
      for (ColeccionDTO coleccion : colecciones) {
        if (coleccion.getHechos() != null) {
          coleccion.getHechos().forEach(h ->
              h.setId(idService.getId(idFuente, h.getId()))
          );
        }
      }

      resultados.addAll(colecciones);
    }

    return ResponseEntity.of(Optional.of(resultados));
  }

  public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      String handle,
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
      String ubicacion,
      String modo
  ) {
    for (IFuente fuente : fuentes) {
      ResponseEntity<List<HechoDTO>> respuesta = fuente.getHechosColeccionByHandle(
          handle,
          categoria,
          fecha_reporte_desde, fecha_reporte_hasta,
          fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
          ubicacion, modo
      );

      List<HechoDTO> hechos = respuesta.getBody();

      // Setear ID unico para cada hecho
      if (hechos != null) {
        Long idFuente = fuente.getId();
        hechos.forEach(h -> {
          if (h.getId() != null) {
            h.setId(idService.getId(idFuente, h.getId()));
          } else {
            log.warn("Hecho sin id recibido desde fuente {}", idFuente);
          }
        });

        return ResponseEntity.ok(hechos);
      }
    }

    return ResponseEntity.notFound().build();
  }

  public IFuente getFuenteById(Long idFuente) {
    return fuentes.stream()
        .filter(fuente -> fuente.getId().equals(idFuente))
        .findFirst()
        .orElse(null);
  }

  public int cantidadDeFuentes() {
    return fuentes.size();
  }
}
