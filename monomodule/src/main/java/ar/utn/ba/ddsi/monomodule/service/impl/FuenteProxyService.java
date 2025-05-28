package ar.utn.ba.ddsi.monomodule.service.impl;

import ar.utn.ba.ddsi.monomodule.domain.dto.external.HechoProxyDTO;
import ar.utn.ba.ddsi.monomodule.domain.dto.external.HechoProxyResponseDTO;
import ar.utn.ba.ddsi.monomodule.domain.dto.external.LoginResponseDTO;
import ar.utn.ba.ddsi.monomodule.domain.hecho.Hecho;
import ar.utn.ba.ddsi.monomodule.exception.ApiLoginException;
import ar.utn.ba.ddsi.monomodule.service.IFuenteService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class FuenteProxyService implements IFuenteService {
  private final WebClient webClient;
  private String accessToken;
  private String tokenType;
  private static final String AUTH_HEADER = "Authorization";

  @Value("${proxy.auth.email}")
  private String email;

  @Value("${proxy.auth.password}")
  private String password;

  public FuenteProxyService(WebClient.Builder webClientBuilder) {
    this.webClient = webClientBuilder.baseUrl("https://api-ddsi.disilab.ar/public/api").build();
  }

  @PostConstruct
  public void init() {
    this.obtenerToken()
        .doOnNext(response -> {
          this.accessToken = response.getData().getAccess_token();
          this.tokenType = response.getData().getToken_type();
        })
        .doOnError(error -> {
          throw new ApiLoginException("No se pudo autenticar con el servicio externo", error);
        })
        .block();
  }

  public Mono<LoginResponseDTO> obtenerToken() {
    return webClient
        .post()
        .uri("/login")
        .bodyValue(Map.of("email", email, "password", password))
        .retrieve()
        .bodyToMono(LoginResponseDTO.class)
        .flatMap(res -> {
          if (!res.isError()) {
            return Mono.just(res);
          } else {
            return Mono.error(new ApiLoginException("Login fallido: " + res.getMessage()));
          }
        });
  }

  public Mono<List<HechoProxyDTO>> getAllHechos() {
    return webClient
        .get()
        .uri("/desastres")
        .header(AUTH_HEADER, tokenType + " " + accessToken)
        .retrieve()
        .bodyToMono(HechoProxyResponseDTO.class)
        .map(HechoProxyResponseDTO::getData);
  }

  public Mono<List<HechoProxyDTO>> getProductsWithParams(Integer page, Integer per_page) {
    return webClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/desastres")
            .queryParam("page", page)
            .queryParam("per_page", per_page)
            .build())
        .header(AUTH_HEADER, tokenType + " " + accessToken)
        .retrieve()
        .bodyToMono(HechoProxyResponseDTO.class)
        .map(HechoProxyResponseDTO::getData);
  }

  public Mono<HechoProxyDTO> getProductById(Long id) {
    return webClient
        .get()
        .uri("/desastres/{id}", id)
        .header(AUTH_HEADER, tokenType + " " + accessToken)
        .retrieve()
        .bodyToMono(HechoProxyDTO.class);
  }

  @Override
  public List<Hecho> importarHechos() throws Exception {
    return List.of();
  }

  @Override
  public void abrirFuente() throws Exception {

  }

  @Override
  public void cerrarFuente() {

  }
}
