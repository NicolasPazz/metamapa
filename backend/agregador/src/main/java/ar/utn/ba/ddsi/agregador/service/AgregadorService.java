package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.ubicacion.Provincia;
import ar.utn.ba.ddsi.agregador.dto.mapper.HechoMapper;
import ar.utn.ba.ddsi.agregador.repositorie.IHechosRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgregadorService {
  private final FuentesService fuentesService;
  private final CategoriasService categoriasService;
  private final IHechosRepository hechosRepository;
  private final HechoMapper hechoMapper;
  private final ProvinciaService provinciaService;
  private final ColeccionesService coleccionesService;
  private final CacheManager cacheManager;

  public void actualizarHechos() {
    try {
      List<HechoDTO> nuevosDTOs = new ArrayList<>();

      try {
        nuevosDTOs = Optional.ofNullable(
            fuentesService.getAllHechosNuevos().getBody()
        ).orElse(List.of());
      } catch (Exception e) {
        log.warn("[AgregadorService] Error al obtener hechos nuevos de fuentes: {}", e.getMessage());
      }

      if (nuevosDTOs.isEmpty()) {
        return;
      }

      List<Hecho> hechosNuevos = hechoMapper.fromDTO(nuevosDTOs);

      // Asignar provincia a hechos que no la tienen (usando coordenadas)
      for (Hecho hecho : hechosNuevos) {
        if (hecho.getLatitud() != null && hecho.getLongitud() != null && hecho.getProvincia() == null) {
          Provincia provincia =
              provinciaService.obtenerProvinciaPorCoordenadas(hecho.getLatitud(), hecho.getLongitud());
          hecho.asignarProvinciaSiNoTiene(provincia);
        }
      }

      // Normalizar y persistir categorías antes de guardar hechos (evita duplicados)
      categoriasService.persistirCategoriasDesdeHechos(hechosNuevos);
      hechosRepository.saveAll(hechosNuevos);
      log.info("[AgregadorService] Se guardaron {} hechos nuevos", hechosNuevos.size());

      // Invalidar caché de primera página cuando se guardan hechos nuevos desde fuentes externas
      if (cacheManager != null) {
        var cache = cacheManager.getCache("hechosPrimeraPagina");
        if (cache != null) {
          cache.clear();
          log.debug("[AgregadorService] Caché de primera página invalidada después de guardar {} hechos nuevos", hechosNuevos.size());
        }
      }

    } catch (Exception e) {
      log.error("[AgregadorService] Error al actualizar hechos: {}", e.getMessage(), e);
    }
  }

  public Map<String, Object> ejecutarScheduler(
      boolean obtenerHechosNuevos,
      boolean recalcularCriterios,
      boolean recalcularConsenso) {
    
    Map<String, Object> resultado = new HashMap<>();
    long tiempoInicio = System.currentTimeMillis();
    
    try {
      if (obtenerHechosNuevos) {
        long inicioHechos = System.currentTimeMillis();
        actualizarHechos();
        long tiempoHechos = System.currentTimeMillis() - inicioHechos;
        resultado.put("hechosNuevos", Map.of(
          "ejecutado", true,
          "tiempoMs", tiempoHechos,
          "mensaje", "Hechos nuevos obtenidos correctamente"
        ));
      } else {
        resultado.put("hechosNuevos", Map.of(
          "ejecutado", false,
          "mensaje", "Omitido por parámetro"
        ));
      }
      
      if (recalcularCriterios || recalcularConsenso) {
        long inicioConsenso = System.currentTimeMillis();
        coleccionesService.actualizarHechosSegunConsenso();
        long tiempoConsenso = System.currentTimeMillis() - inicioConsenso;
        resultado.put("recalcularCriteriosYConsenso", Map.of(
          "ejecutado", true,
          "tiempoMs", tiempoConsenso,
          "mensaje", "Criterios de pertenencia y hechos consensuados recalculados correctamente"
        ));
      } else {
        resultado.put("recalcularCriteriosYConsenso", Map.of(
          "ejecutado", false,
          "mensaje", "Omitido por parámetro"
        ));
      }
      
      long tiempoTotal = System.currentTimeMillis() - tiempoInicio;
      resultado.put("tiempoTotalMs", tiempoTotal);
      resultado.put("exito", true);
      resultado.put("mensaje", "Scheduler ejecutado correctamente");
      
      return resultado;
      
    } catch (Exception e) {
      log.error("[AgregadorService] Error al ejecutar scheduler: {}", e.getMessage(), e);
      resultado.put("exito", false);
      resultado.put("error", e.getMessage());
      resultado.put("tiempoTotalMs", System.currentTimeMillis() - tiempoInicio);
      throw new RuntimeException("Error al ejecutar scheduler: " + e.getMessage(), e);
    }
  }
}
