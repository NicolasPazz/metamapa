package ar.utn.ba.ddsi.estadistica.client;

import ar.utn.ba.ddsi.estadistica.domain.dto.input.ColeccionDTO;
import ar.utn.ba.ddsi.estadistica.domain.dto.input.HechoDTO;
import ar.utn.ba.ddsi.estadistica.domain.dto.input.SolicitudDTO;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class AgregadorClient {

  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${agregador.base-url}")
  private String baseUrl;

  public List<HechoDTO> getAllHechos() {
    log.info("Obteniendo todos los hechos desde el agregador");
    try {
      // Obtener todas las colecciones y extraer todos los hechos
      List<ColeccionDTO> colecciones = getAllColecciones();

      List<HechoDTO> todosLosHechos = colecciones.stream()
          .flatMap(coleccion -> coleccion.getHechos().stream())
          .toList();

      log.info("Se obtuvieron {} hechos de {} colecciones", todosLosHechos.size(), colecciones.size());
      return todosLosHechos;
    } catch (Exception e) {
      log.error("Error al obtener hechos: {}", e.getMessage());
      return List.of();
    }
  }

  public List<ColeccionDTO> getAllColecciones() {
    log.info("Obteniendo todas las colecciones desde el agregador");
    try {
      String url = baseUrl + "/colecciones";
      ResponseEntity<List<ColeccionDTO>> response = restTemplate.exchange(
          url,
          HttpMethod.GET,
          null,
          new ParameterizedTypeReference<List<ColeccionDTO>>() {
          }
      );
      List<ColeccionDTO> colecciones = response.getBody();
      log.info("Se obtuvieron {} colecciones", colecciones != null ? colecciones.size() : 0);
      return colecciones != null ? colecciones : List.of();
    } catch (Exception e) {
      log.error("Error al obtener colecciones: {}", e.getMessage());
      return List.of();
    }
  }

  public List<SolicitudDTO> getAllSolicitudes() {
    log.info("Obteniendo todas las solicitudes desde el agregador");
    try {
      String url = baseUrl + "/admin/solicitudes";
      ResponseEntity<List<SolicitudDTO>> response = restTemplate.exchange(
          url,
          HttpMethod.GET,
          null,
          new ParameterizedTypeReference<List<SolicitudDTO>>() {
          }
      );
      List<SolicitudDTO> solicitudes = response.getBody();
      log.info("Se obtuvieron {} solicitudes", solicitudes != null ? solicitudes.size() : 0);
      return solicitudes != null ? solicitudes : List.of();
    } catch (Exception e) {
      log.error("Error al obtener solicitudes: {}", e.getMessage());
      return List.of();
    }
  }
}
