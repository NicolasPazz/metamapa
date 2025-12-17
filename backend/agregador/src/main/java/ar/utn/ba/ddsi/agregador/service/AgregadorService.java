package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.dto.mapper.HechoMapper;
import ar.utn.ba.ddsi.agregador.repositorie.IHechosRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgregadorService {
  private final FuentesService fuentesService;
  private final CategoriasService categoriasService;
  private final IHechosRepository hechosRepository;
  private final HechoMapper hechoMapper;

  public void actualizarHechos() {
    try {
      log.info("🔄 [AgregadorService] ====== INICIANDO ACTUALIZACIÓN DE HECHOS ======");

      log.info("🔄 [AgregadorService] Llamando a FuentesService.getAllHechosNuevos()...");
      List<HechoDTO> nuevosDTOs = new ArrayList<>();

      try {
        nuevosDTOs = Optional.ofNullable(
            fuentesService.getAllHechosNuevos().getBody()
        ).orElse(List.of());
      } catch (Exception e) {
        log.warn("⚠️ [AgregadorService] Error al obtener hechos nuevos de fuentes (pueden estar caídas): {}",
            e.getMessage());
        log.debug("⚠️ [AgregadorService] Stack trace: ", e);
      }

      log.info("🔄 [AgregadorService] Cantidad de hechos nuevos obtenidos: {}", nuevosDTOs.size());

      if (nuevosDTOs.isEmpty()) {
        log.warn("⚠️ [AgregadorService] No se obtuvieron hechos nuevos de ninguna fuente (o las fuentes están caídas)");
        log.info("✅ [AgregadorService] ====== ACTUALIZACIÓN DE HECHOS COMPLETADA (sin cambios) ======");
        return; // No hay nada que procesar
      }

      List<Hecho> hechosNuevos = hechoMapper.fromDTO(nuevosDTOs);

      categoriasService.persistirCategoriasDesdeHechos(hechosNuevos);

      hechosRepository.saveAll(hechosNuevos);
      log.info("💾 [AgregadorService] Se guardaron {} hechos nuevos.", hechosNuevos.size());


      log.info("✅ [AgregadorService] ====== ACTUALIZACIÓN DE HECHOS COMPLETADA ======");

    } catch (Exception e) {
      log.error("❌ [AgregadorService] Error al actualizar hechos: {}", e.getMessage(), e);
    }
  }
}
