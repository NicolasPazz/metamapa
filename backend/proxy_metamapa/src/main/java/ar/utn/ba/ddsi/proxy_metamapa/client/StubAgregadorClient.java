package ar.utn.ba.ddsi.proxy_metamapa.client;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.ColeccionDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.HechoDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@Profile({"dev"})
@RequiredArgsConstructor
public class StubAgregadorClient implements AgregadorClient {

  private final StubDataFixture fixture;

  @Override
  public ResponseEntity<List<HechoDTO>> getAllHechosVisibles(String categoria,
                                                             LocalDate frd, LocalDate frh,
                                                             LocalDate fad, LocalDate fah,
                                                             String ubicacion) {

    List<HechoDTO> filtrados = fixture.getHechos().stream()

        .filter(h -> categoria == null ||
            h.getCategoria() != null &&
                h.getCategoria().toLowerCase().contains(categoria.toLowerCase()))

        .filter(h -> frd == null ||
            (h.getFechaDeCarga() != null &&
                !h.getFechaDeCarga().toLocalDate().isBefore(frd)))

        .filter(h -> frh == null ||
            (h.getFechaDeCarga() != null &&
                !h.getFechaDeCarga().toLocalDate().isAfter(frh)))

        .filter(h -> fad == null ||
            (h.getFechaDeAcontecimiento() != null &&
                !h.getFechaDeAcontecimiento().isBefore(fad)))

        .filter(h -> fah == null ||
            (h.getFechaDeAcontecimiento() != null &&
                !h.getFechaDeAcontecimiento().isAfter(fah)))

        .filter(h -> {
          if (ubicacion == null) return true;
          String current = h.getLatitud() + "," + h.getLongitud();
          return ubicacion.equals(current);
        })

        .toList();

    return ResponseEntity.of(Optional.of(filtrados));
  }

  @Override
  public ResponseEntity<List<ColeccionDTO>> getAllColecciones() {
    return ResponseEntity.of(Optional.of(fixture.getColecciones()));
  }

  @Override
  public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      String handle,
      String categoria,
      LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
      LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
      String ubicacion
  ) {
    log.info("Buscando hechos de la colección con handle: {}", handle);

    java.util.UUID uuidHandle;
    try {
      uuidHandle = java.util.UUID.fromString(handle);
    } catch (IllegalArgumentException e) {
      log.error("Handle inválido: {}", handle);
      return ResponseEntity.notFound().build();
    }

    ColeccionDTO encontrada = fixture.getColecciones().stream()
        .filter(c -> c.getHandle().equals(uuidHandle))
        .findFirst()
        .orElse(null);

    if (encontrada == null) {
      return ResponseEntity.notFound().build();
    }

    List<HechoDTO> hechosFiltrados = Optional.ofNullable(encontrada.getHechos())
        .orElse(List.of())
        .stream()
        .filter(h -> categoria == null || (h.getCategoria() != null && h.getCategoria().toLowerCase().contains(categoria.toLowerCase())))
        .filter(h -> ubicacion == null || (h.getLatitud() + "," + h.getLongitud()).equals(ubicacion))
        .filter(h -> fecha_reporte_desde == null || !h.getFechaDeCarga().toLocalDate().isBefore(fecha_reporte_desde))
        .filter(h -> fecha_reporte_hasta == null || !h.getFechaDeCarga().toLocalDate().isAfter(fecha_reporte_hasta))
        .filter(h -> fecha_acontecimiento_desde == null || !h.getFechaDeAcontecimiento().isBefore(fecha_acontecimiento_desde))
        .filter(h -> fecha_acontecimiento_hasta == null || !h.getFechaDeAcontecimiento().isAfter(fecha_acontecimiento_hasta))
        .toList();

    return ResponseEntity.ok(hechosFiltrados);
  }
}