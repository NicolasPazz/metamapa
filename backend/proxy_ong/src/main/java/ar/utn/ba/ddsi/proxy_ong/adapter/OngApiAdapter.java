package ar.utn.ba.ddsi.proxy_ong.adapter;

import ar.utn.ba.ddsi.proxy_ong.domain.dto.external.HechoProxyDTO;
import ar.utn.ba.ddsi.proxy_ong.domain.dto.external.HechoProxyResponseDTO;
import ar.utn.ba.ddsi.proxy_ong.domain.dto.external.LoginResponseDTO;
import ar.utn.ba.ddsi.proxy_ong.domain.hecho.Hecho;
import ar.utn.ba.ddsi.proxy_ong.exception.ApiLoginException;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class OngApiAdapter implements IApiExternaAdapter {

  private final WebClient webClient;
  private String accessToken;
  private String tokenType;
  private final Integer perPage = 100;
  private final String authHeader = "Authorization";
  private String authHeaderValue;

  @Value("${proxy_ong.auth.email}")
  private String email;

  @Value("${proxy_ong.auth.password}")
  private String password;

  public OngApiAdapter(
      WebClient.Builder webClientBuilder,
      @Value("${proxy_ong.api.url}") String apiUrl
  ) {
    this.webClient = webClientBuilder.baseUrl(apiUrl).build();
  }

  @PostConstruct
  private void inicializar() {
    try {
      autenticar();
    } catch (Exception e) {
      throw new ApiLoginException("No se pudo autenticar con el servicio externo", e);
    }
  }

  @Override
  public List<Hecho> obtenerTodosLosHechos() {
    List<HechoProxyDTO> dtos = obtenerHechosDesdeApi();
    return convertirDtosADominio(dtos);
  }

  @Override
  public boolean verificarConexion() {
    try {
      autenticar();
      return true;
    } catch (Exception e) {
      log.error("Error al verificar conexión con la API externa", e);
      return false;
    }
  }

  private void autenticar() {
    LoginResponseDTO response = webClient
        .post()
        .uri("/login")
        .bodyValue(Map.of("email", email, "password", password))
        .retrieve()
        .bodyToMono(LoginResponseDTO.class)
        .block();

    if (response == null || response.isError()) {
      throw new ApiLoginException("Login fallido: " +
          (response != null ? response.getMessage() : "respuesta nula"));
    }

    this.accessToken = response.getData().getAccess_token();
    this.tokenType = response.getData().getToken_type();
    this.authHeaderValue = tokenType + " " + accessToken;
    log.info("Autenticación exitosa con la API externa de ONG");
  }

  private List<HechoProxyDTO> obtenerHechosDesdeApi() {
    HechoProxyResponseDTO firstPage = obtenerPagina(1);

    if (firstPage == null || firstPage.getData() == null) {
      return List.of();
    }

    List<HechoProxyDTO> todos = new ArrayList<>(firstPage.getData());
    Long lastPage = firstPage.getLast_page();

    for (int page = 2; page <= lastPage; page++) {
      HechoProxyResponseDTO pageResult = obtenerPagina(page);
      if (pageResult != null && pageResult.getData() != null) {
        todos.addAll(pageResult.getData());
      }
    }

    log.info("Se obtuvieron {} hechos de la API externa", todos.size());
    return todos;
  }

  private HechoProxyResponseDTO obtenerPagina(int page) {
    return webClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/desastres")
            .queryParam("per_page", perPage)
            .queryParam("page", page)
            .build())
        .header(authHeader, authHeaderValue)
        .retrieve()
        .bodyToMono(HechoProxyResponseDTO.class)
        .block();
  }

  private List<Hecho> convertirDtosADominio(List<HechoProxyDTO> dtos) {
    return dtos.stream()
        .map(this::convertirDtoADominio)
        .toList();
  }

  private Hecho convertirDtoADominio(HechoProxyDTO dto) {
    return new Hecho(
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
  }
}

