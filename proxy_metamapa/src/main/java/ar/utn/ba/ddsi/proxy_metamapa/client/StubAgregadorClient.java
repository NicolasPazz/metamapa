package ar.utn.ba.ddsi.proxy_metamapa.client;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.ColeccionDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.HechoDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.input.SolicitudInputDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.SolicitudOutputDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
  public ResponseEntity<List<HechoDTO>> getColeccionByHandle(String handle) {
    ColeccionDTO encontrada = fixture.getColecciones().stream()
        .filter(c -> c.getHandle().equals(handle))
        .findFirst()
        .orElse(null);

    if (encontrada == null) {
      return ResponseEntity.notFound().build();
    }

    List<HechoDTO> hechos = Optional.ofNullable(encontrada.getHechos()).orElse(List.of());
    return ResponseEntity.ok(hechos);
  }


  @Override
  public ResponseEntity<SolicitudOutputDTO> crearSolicitudDeEliminacion(SolicitudInputDTO solicitud) {
    SolicitudOutputDTO solicitudOutputDTO = new SolicitudOutputDTO();
    solicitudOutputDTO.setId(1L);
    solicitudOutputDTO.setIdHecho(solicitud.getIdHecho());
    solicitudOutputDTO.setTextoFundamentos(solicitud.getTextoFundamentos());
    solicitudOutputDTO.setFecha(LocalDate.now());
    solicitudOutputDTO.setEstado("PENDIENTE");
    solicitudOutputDTO.setIdContribuyente(solicitud.getIdContribuyente());

    return ResponseEntity.of(Optional.of(solicitudOutputDTO));
  }
}