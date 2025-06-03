package ar.utn.ba.ddsi.proxy_metamapa.service.impl;

import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.external.HechoProxyDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.external.HechoProxyResponseDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.external.LoginResponseDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.proxy_metamapa.domain.hecho.Hecho;
import ar.utn.ba.ddsi.proxy_metamapa.domain.repositorie.impl.HechosRepository;
import ar.utn.ba.ddsi.proxy_metamapa.exception.ApiLoginException;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FuenteService {
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

  public FuenteService(WebClient.Builder webClientBuilder, @Value("${proxy_ong.api.url}") String apiUrl) {
    this.webClient = webClientBuilder.baseUrl(apiUrl).build();
  }

  @PostConstruct
  private void init() {
    this.obtenerToken()
        .doOnNext(response -> {
          this.accessToken = response.getData().getAccess_token();
          this.tokenType = response.getData().getToken_type();
          AUTH_HEADER_VALUE = tokenType + " " + accessToken;
        })
        .doOnError(error -> {
          throw new ApiLoginException("No se pudo autenticar con el servicio externo", error);
        })
        .block();
  }

  private Mono<LoginResponseDTO> obtenerToken() {
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

  private Mono<List<HechoProxyDTO>> getAllHechos() {
    return webClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .path("/desastres")
            .queryParam("per_page", per_page)
            .queryParam("page", 1)
            .build())
        .header(AUTH_HEADER, AUTH_HEADER_VALUE)
        .retrieve()
        .bodyToMono(HechoProxyResponseDTO.class)
        .flatMapMany(firstPage -> {
          long lastPage = firstPage.getLast_page();

          Flux<HechoProxyDTO> pagina1 = Flux.fromIterable(firstPage.getData());

          Flux<HechoProxyDTO> resto = Flux.range(2, (int) (lastPage))
              .flatMap(page ->
                  webClient
                      .get()
                      .uri(uriBuilder -> uriBuilder
                          .path("/desastres")
                          .queryParam("per_page", per_page)
                          .queryParam("page", page)
                          .build())
                      .header(AUTH_HEADER, AUTH_HEADER_VALUE)
                      .retrieve()
                      .bodyToMono(HechoProxyResponseDTO.class)
                      .map(HechoProxyResponseDTO::getData)
                      .flatMapMany(Flux::fromIterable)
              );

          return pagina1.concatWith(resto);
        })
        .collectList();
  }

  public List<HechoOutputDTO> getAllHechosVisibles() {
    return List.of();
  }

  public Mono<List<HechoOutputDTO>> getAllHechosNuevos() {
    return this.getAllHechos()
        .flatMapMany(Flux::fromIterable)
        .filter(dto -> !hechosRepository.existsById(dto.getId())) // Asumo que las API que se consumiran, no eliminan ni modifican hechos, solo agregan nuevos.
        .map(dto -> {
          hechosRepository.save(new Hecho(
              dto.getId(),
              dto.getTitulo(),
              dto.getDescripcion(),
              dto.getLatitud(),
              dto.getLongitud(),
              dto.getFecha_hecho(),
              dto.getCreated_at(),
              dto.getUpdated_at()
          ));

          return new HechoOutputDTO(
              dto.getId(),
              dto.getTitulo(),
              dto.getDescripcion(),
              dto.getLatitud(),
              dto.getLongitud(),
              dto.getFecha_hecho(),
              dto.getCreated_at(),
              dto.getUpdated_at()
          );
        })
        .collectList();
  }
}
