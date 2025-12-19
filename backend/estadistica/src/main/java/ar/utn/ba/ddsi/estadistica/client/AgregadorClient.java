package ar.utn.ba.ddsi.estadistica.client;

import ar.utn.ba.ddsi.estadistica.dto.input.ColeccionDTO;
import ar.utn.ba.ddsi.estadistica.dto.input.HechoDTO;
import ar.utn.ba.ddsi.estadistica.dto.input.SolicitudDTO;
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

  @Value("${agregador.base-url:http://localhost:8084/api}")
  private String baseUrl;

  public List<HechoDTO> getAllHechos() {
    log.info("📊 [AgregadorClient] Obteniendo todos los hechos desde el agregador");
    try {
      String url = baseUrl + "/hechos";
      log.debug("🔗 [AgregadorClient] URL: {}", url);

      ResponseEntity<List<HechoDTO>> response = restTemplate.exchange(
          url,
          HttpMethod.GET,
          null,
          new ParameterizedTypeReference<>() {}
      );

      List<HechoDTO> hechos = response.getBody();
      log.info("✅ [AgregadorClient] Se obtuvieron {} hechos", hechos != null ? hechos.size() : 0);

      if (hechos != null && !hechos.isEmpty()) {
        HechoDTO primerHecho = hechos.get(0);
        log.debug("📝 [AgregadorClient] Primer hecho - ID: {}, Categoria: {}, Lat: {}, Lon: {}",
            primerHecho.getId(),
            primerHecho.getCategoria(),
            primerHecho.getLatitud(),
            primerHecho.getLongitud());
      }

      return hechos != null ? hechos : List.of();
    } catch (Exception e) {
      log.error("❌ [AgregadorClient] Error al obtener hechos: {}", e.getMessage());
      log.error("❌ [AgregadorClient] Tipo de error: {}", e.getClass().getName());
      if (e.getCause() != null) {
        log.error("❌ [AgregadorClient] Causa: {}", e.getCause().getMessage());
      }
      return List.of();
    }
  }

  public List<ColeccionDTO> getAllColecciones() {
    log.info("📊 [AgregadorClient] Obteniendo todas las colecciones desde el agregador");
    try {
      String url = baseUrl + "/colecciones";
      ResponseEntity<List<ColeccionDTO>> response = restTemplate.exchange(
          url,
          HttpMethod.GET,
          null,
          new ParameterizedTypeReference<>() {}
      );
      List<ColeccionDTO> colecciones = response.getBody();
      log.info("✅ [AgregadorClient] Se obtuvieron {} colecciones", colecciones != null ? colecciones.size() : 0);
      return colecciones != null ? colecciones : List.of();
    } catch (Exception e) {
      log.error("❌ [AgregadorClient] Error al obtener colecciones: {}", e.getMessage());
      return List.of();
    }
  }

  public List<SolicitudDTO> getAllSolicitudes() {
    log.info("📊 [AgregadorClient] Obteniendo todas las solicitudes desde el agregador");
    try {
      String url = baseUrl + "/admin/solicitudes";
      ResponseEntity<List<SolicitudDTO>> response = restTemplate.exchange(
          url,
          HttpMethod.GET,
          null,
          new ParameterizedTypeReference<>() {}
      );
      List<SolicitudDTO> solicitudes = response.getBody();
      log.info("✅ [AgregadorClient] Se obtuvieron {} solicitudes", solicitudes != null ? solicitudes.size() : 0);
      return solicitudes != null ? solicitudes : List.of();
    } catch (Exception e) {
      log.error("❌ [AgregadorClient] Error al obtener solicitudes: {}", e.getMessage());
      return List.of();
    }
  }
}
