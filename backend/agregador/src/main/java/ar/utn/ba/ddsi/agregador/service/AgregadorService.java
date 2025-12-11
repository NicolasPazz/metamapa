package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.dto.mapper.HechoMapper;
import ar.utn.ba.ddsi.agregador.repositorie.IHechosRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgregadorService {
  private final FuentesService fuentesService;
  private final CategoriasService categoriasService;
  private final IHechosRepository hechosRepository;
  private final HechoMapper hechoMapper;

  @Value("${estadisticas.base-url}")
  private String baseUrl;

  public void actualizarHechos() {
    try {
      WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();

      log.info("[Agregador] Iniciando actualización de hechos...");

      List<HechoDTO> nuevosDTOs = Optional.ofNullable(
          fuentesService.getAllHechosNuevos().getBody()
      ).orElse(List.of());

      log.info("[Agregador] Cantidad de hechos nuevos obtenidos: {}", nuevosDTOs.size());

      List<Hecho> hechosNuevos = hechoMapper.fromDTO(nuevosDTOs);

      categoriasService.persistirCategoriasDesdeHechos(hechosNuevos);

      hechosRepository.saveAll(hechosNuevos);
      log.info("[Agregador] Se guardaron {} hechos nuevos.", hechosNuevos.size());

      log.info("Aviso a Estadísticas…");

      webClient.post()
          .uri("/recalcular")
          .retrieve()
          .toBodilessEntity()
          .doOnError(ex -> log.warn("⚠️ Aviso a Estadísticas falló: {}", ex.toString()))
          .subscribe();

    } catch (Exception e) {
      log.error("[Agregador] Error al actualizar hechos: {}", e.getMessage(), e);
    }
  }
}
