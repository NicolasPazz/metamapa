package ar.utn.ba.ddsi.proxy_ong.service;

import ar.utn.ba.ddsi.proxy_ong.adapter.IApiExternaAdapter;
import ar.utn.ba.ddsi.proxy_ong.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.proxy_ong.domain.hecho.Hecho;
import ar.utn.ba.ddsi.proxy_ong.domain.repositorie.IHechosRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

@Slf4j
@Service
public class FuenteService {

  private final IApiExternaAdapter apiAdapter;
  private final IHechosRepository hechosRepository;

  public FuenteService(
      IApiExternaAdapter apiAdapter,
      IHechosRepository hechosRepository
  ) {
    this.apiAdapter = apiAdapter;
    this.hechosRepository = hechosRepository;
  }

  public ResponseEntity<List<HechoOutputDTO>> getAllHechosNuevos() {
    try {
      List<Hecho> todosLosHechos = apiAdapter.obtenerTodosLosHechos();
      List<HechoOutputDTO> hechosNuevos = procesarHechosNuevos(todosLosHechos);

      return ResponseEntity.of(Optional.of(hechosNuevos));

    } catch (Exception e) {
      log.error("Error al obtener hechos nuevos desde el proxy ONG", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  private List<HechoOutputDTO> procesarHechosNuevos(List<Hecho> hechos) {
    List<HechoOutputDTO> nuevos = new ArrayList<>();

    for (Hecho hecho : hechos) {
      if (esHechoNuevo(hecho)) {
        registrarHecho(hecho);
        nuevos.add(convertirAOutputDTO(hecho));
      }
    }

    log.info("Se procesaron {} hechos nuevos de un total de {}", nuevos.size(), hechos.size());
    return nuevos;
  }

  private boolean esHechoNuevo(Hecho hecho) {
    return !hechosRepository.existsById(hecho.getId());
  }

  private void registrarHecho(Hecho hecho) {
    hechosRepository.save(hecho);
    log.debug("Hecho registrado con ID: {}", hecho.getId());
  }

  private HechoOutputDTO convertirAOutputDTO(Hecho hecho) {
    return new HechoOutputDTO(
        hecho.getId(),
        "ONG",
        hecho.getTitulo(),
        hecho.getDescripcion(),
        hecho.getCategoria(),
        hecho.getLatitud(),
        hecho.getLongitud(),
        hecho.getFechaDeAcontecimiento(),
        hecho.getFechaDeCarga()
    );
  }
}
