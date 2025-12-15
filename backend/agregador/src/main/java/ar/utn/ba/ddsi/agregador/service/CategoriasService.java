package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.hecho.Categoria;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.repositorie.ICategoriaRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriasService {

  private final ICategoriaRepository categoriaRepository;

  public void persistirCategoriasDesdeHechos(List<Hecho> hechos) {
    if (hechos == null || hechos.isEmpty()) {
      log.debug("[CategoriasService] No hay hechos para procesar");
      return;
    }

    log.info("[CategoriasService] Iniciando normalización de categorías para {} hechos", hechos.size());

    // Paso 1: Extraer nombres normalizados de las categorías
    Set<String> nombresNormalizados = extraerNombresNormalizados(hechos);
    log.debug("[CategoriasService] Se encontraron {} categorías únicas", nombresNormalizados.size());

    // Paso 2: Cargar categorías existentes desde la base de datos
    Map<String, Categoria> categoriasExistentes = cargarCategoriasExistentes(nombresNormalizados);
    log.debug("[CategoriasService] Se encontraron {} categorías existentes en BD", categoriasExistentes.size());

    // Paso 3: Asignar categorías a los hechos y detectar nuevas categorías
    Map<String, Categoria> categoriasNuevas = asignarCategoriasAHechos(hechos, categoriasExistentes);
    log.debug("[CategoriasService] Se detectaron {} categorías nuevas", categoriasNuevas.size());

    // Paso 4: Persistir nuevas categorías y actualizar referencias
    if (!categoriasNuevas.isEmpty()) {
      persistirNuevasCategorias(hechos, categoriasNuevas, categoriasExistentes);
      log.info("[CategoriasService] Se persistieron {} categorías nuevas", categoriasNuevas.size());
    }

    log.info("[CategoriasService] Normalización de categorías completada exitosamente");
  }

  private Set<String> extraerNombresNormalizados(List<Hecho> hechos) {
    return hechos.stream()
        .map(Hecho::getCategoria)
        .filter(Objects::nonNull)
        .map(Categoria::getNombre)
        .filter(Objects::nonNull)
        .map(this::normalizarNombre)
        .collect(Collectors.toSet());
  }

  private Map<String, Categoria> cargarCategoriasExistentes(Set<String> nombresNormalizados) {
    Map<String, Categoria> categoriasMap = new HashMap<>();

    if (nombresNormalizados.isEmpty()) {
      return categoriasMap;
    }

    List<Categoria> existentes = categoriaRepository.findByNombreIn(new ArrayList<>(nombresNormalizados));
    existentes.forEach(categoria ->
        categoriasMap.put(normalizarNombre(categoria.getNombre()), categoria)
    );

    return categoriasMap;
  }

  private Map<String, Categoria> asignarCategoriasAHechos(
      List<Hecho> hechos,
      Map<String, Categoria> categoriasExistentes) {

    Map<String, Categoria> categoriasNuevas = new HashMap<>();

    for (Hecho hecho : hechos) {
      Categoria categoriaActual = hecho.getCategoria();

      if (categoriaActual == null || categoriaActual.getNombre() == null) {
        continue;
      }

      String nombreNormalizado = normalizarNombre(categoriaActual.getNombre());

      // Caso 1: La categoría ya existe en BD - reutilizar
      if (categoriasExistentes.containsKey(nombreNormalizado)) {
        hecho.setCategoria(categoriasExistentes.get(nombreNormalizado));
        continue;
      }

      // Caso 2: Ya procesamos esta categoría nueva anteriormente - reutilizar
      if (categoriasNuevas.containsKey(nombreNormalizado)) {
        hecho.setCategoria(categoriasNuevas.get(nombreNormalizado));
        continue;
      }

      // Caso 3: Es una categoría nueva - crear y registrar
      Categoria nuevaCategoria = new Categoria(nombreNormalizado);
      categoriasNuevas.put(nombreNormalizado, nuevaCategoria);
      hecho.setCategoria(nuevaCategoria);
    }

    return categoriasNuevas;
  }

  private void persistirNuevasCategorias(
      List<Hecho> hechos,
      Map<String, Categoria> categoriasNuevas,
      Map<String, Categoria> categoriasExistentes) {

    // Persistir en batch todas las categorías nuevas
    List<Categoria> categoriasPersistidas = categoriaRepository.saveAll(
        new ArrayList<>(categoriasNuevas.values())
    );

    // Actualizar el mapa de existentes con las recién persistidas
    categoriasPersistidas.forEach(categoria ->
        categoriasExistentes.put(normalizarNombre(categoria.getNombre()), categoria)
    );

    // Actualizar referencias en hechos (por si saveAll devolvió instancias distintas con ID)
    for (Hecho hecho : hechos) {
      Categoria categoria = hecho.getCategoria();

      if (categoria != null && categoria.getId() == null) {
        String nombreNormalizado = normalizarNombre(categoria.getNombre());
        hecho.setCategoria(categoriasExistentes.get(nombreNormalizado));
      }
    }
  }

  private String normalizarNombre(String nombre) {
    return nombre.trim().toLowerCase();
  }
}
