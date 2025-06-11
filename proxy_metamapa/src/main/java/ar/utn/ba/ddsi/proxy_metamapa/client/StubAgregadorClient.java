package ar.utn.ba.ddsi.proxy_metamapa.client;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.ColeccionDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.HechoDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.SolicitudDeEliminacionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.util.List;

@Service
@Profile({"dev"})
@RequiredArgsConstructor
public class StubAgregadorClient implements AgregadorClient {

  private final StubDataFixture fixture;

  @Override
  public Mono<List<HechoDTO>> getAllHechosVisibles(String categoria,
                                                   LocalDate frd, LocalDate frh,
                                                   LocalDate fad, LocalDate fah,
                                                   String  ubicacion) {

    return Mono.just(
        fixture.getHechos().stream()

            .filter(h -> categoria == null || categoria.equals(h.getCategoria()))

            .filter(h -> frd == null ||
                (h.getFechaDeCarga() != null &&
                    !h.getFechaDeCarga().toLocalDate().isAfter(frd)))

            .filter(h -> frh == null ||
                (h.getFechaDeCarga() != null &&
                    !h.getFechaDeCarga().toLocalDate().isBefore(frh)))

            .filter(h -> fad == null ||
                (h.getFechaDeAcontecimiento() != null &&
                    !h.getFechaDeAcontecimiento().isAfter(fad)))

            .filter(h -> fah == null ||
                (h.getFechaDeAcontecimiento() != null &&
                    !h.getFechaDeAcontecimiento().isBefore(fah)))

            .filter(h -> {
              if (ubicacion == null) return true;
              String current = h.getLatitud() + "," + h.getLongitud();
              return ubicacion.equals(current);
            })

            .toList()
    );
  }

  @Override
  public Mono<List<ColeccionDTO>> getAllColecciones() {
    return Mono.just(fixture.getColecciones());
  }

  @Override
  public Mono<ColeccionDTO> getColeccionByHandle(String handle) {
    return Mono.just(
        fixture.getColecciones().stream()
            .filter(c -> c.getHandle().equals(handle))
            .findFirst()
            .orElse(null)
    );
  }

  @Override
  public Mono<SolicitudDeEliminacionDTO> crearSolicitudDeEliminacion(SolicitudDeEliminacionDTO solicitud) {
    return Mono.just(solicitud);
  }
}