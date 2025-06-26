package ar.utn.ba.ddsi.proxy_ong.service.impl;

import ar.utn.ba.ddsi.proxy_ong.domain.dto.external.HechoProxyDTO;
import ar.utn.ba.ddsi.proxy_ong.domain.dto.external.HechoProxyResponseDTO;
import ar.utn.ba.ddsi.proxy_ong.domain.dto.external.LoginResponseDTO;
import ar.utn.ba.ddsi.proxy_ong.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.proxy_ong.domain.hecho.Hecho;
import ar.utn.ba.ddsi.proxy_ong.domain.repositorie.impl.HechosRepository;
import ar.utn.ba.ddsi.proxy_ong.exception.ApiLoginException;
import ar.utn.ba.ddsi.proxy_ong.service.IFuenteService;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.ResponseEntity;

@Slf4j
@Service
public class FuenteService implements IFuenteService {
  private final WebClient webClient;
  private String accessToken;
  private String tokenType;
  private final Integer per_page = 100;
  private final String AUTH_HEADER = "Authorization";
  private String AUTH_HEADER_VALUE;

  @Autowired
  private HechosRepository hechosRepository;

  @Value("${proxy_ong.auth.email}")
  private String email;

  @Value("${proxy_ong.auth.password}")
  private String password;

  public FuenteService(
      WebClient.Builder webClientBuilder,
      @Value("${proxy_ong.api.url}") String apiUrl
  ) { this.webClient = webClientBuilder.baseUrl(apiUrl).build(); }

  @PostConstruct
  private void init() {
    try {
      LoginResponseDTO response = obtenerToken();
      this.accessToken = response.getData().getAccess_token();
      this.tokenType = response.getData().getToken_type();
      this.AUTH_HEADER_VALUE = tokenType + " " + accessToken;
    } catch (Exception e) {
      throw new ApiLoginException("No se pudo autenticar con el servicio externo", e);
    }
  }

  private LoginResponseDTO obtenerToken() {
    LoginResponseDTO response = webClient
        .post()
        .uri("/login")
        .bodyValue(Map.of("email", email, "password", password))
        .retrieve()
        .bodyToMono(LoginResponseDTO.class)
        .block();

    if (response == null || response.isError()) {
      throw new ApiLoginException("Login fallido: " + (response != null ? response.getMessage() : "respuesta nula"));
    }

    return response;
  }

  private List<HechoProxyDTO> getAllHechos() {
    // Obtener primera página para saber cuántos hay
    HechoProxyResponseDTO firstPage = webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/desastres")
            .queryParam("per_page", per_page)
            .queryParam("page", 1)
            .build())
        .header(AUTH_HEADER, AUTH_HEADER_VALUE)
        .retrieve()
        .bodyToMono(HechoProxyResponseDTO.class)
        .block();

    if (firstPage == null || firstPage.getData() == null) return List.of();

    List<HechoProxyDTO> todos = new ArrayList<>(firstPage.getData());
    Long lastPage = firstPage.getLast_page();

    for (int page = 2; page <= lastPage; page++) {
      int finalPage = page;
      HechoProxyResponseDTO pageResult = webClient.get()
          .uri(uriBuilder -> uriBuilder
              .path("/desastres")
              .queryParam("per_page", per_page)
              .queryParam("page", finalPage)
              .build())
          .header(AUTH_HEADER, AUTH_HEADER_VALUE)
          .retrieve()
          .bodyToMono(HechoProxyResponseDTO.class)
          .block();

      if (pageResult != null && pageResult.getData() != null) {
        todos.addAll(pageResult.getData());
      }
    }

    return todos;
  }

  /**
   * Devuelve todos los hechos nuevos obtenidos de la fuente externa,
   * que aún no están registrados localmente. Guarda los nuevos hechos
   * en el repositorio local y devuelve su representación de salida.
   *
   * @return Mono con la lista de hechos nuevos
   */
  @Override
  public ResponseEntity<List<HechoOutputDTO>> getAllHechosNuevos() {
    try {
      List<HechoProxyDTO> todos = getAllHechos();

      List<HechoOutputDTO> nuevos = new ArrayList<>();

      for (HechoProxyDTO dto : todos) {
        if (!hechosRepository.existsById(dto.getId())) {
          Hecho hecho = new Hecho(
              dto.getId(),
              dto.getTitulo(),
              dto.getDescripcion(),
              dto.getCategoria(),
              dto.getLatitud(),
              dto.getLongitud(),
              dto.getFecha_hecho(),
              dto.getCreated_at(),
              dto.getUpdated_at()
          );

          hechosRepository.save(hecho);

          nuevos.add(new HechoOutputDTO(
              dto.getId(),
              dto.getTitulo(),
              dto.getDescripcion(),
              dto.getCategoria(),
              dto.getLatitud(),
              dto.getLongitud(),
              dto.getFecha_hecho(),
              dto.getCreated_at(),
              dto.getUpdated_at()
          ));
        }
      }

      return ResponseEntity.of(Optional.of(nuevos));

    } catch (Exception e) {
      log.error("Error al obtener hechos nuevos desde el proxy ONG", e);
      return ResponseEntity.internalServerError().build();
    }
  }
}
