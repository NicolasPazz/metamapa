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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoriasService {

  private final ICategoriaRepository categoriaRepository;

  public void persistirCategoriasDesdeHechos(List<Hecho> hechos) {
    if (hechos == null || hechos.isEmpty()) {
      return;
    }

    // Extraer y normalizar nombres de categorías (trim + lowercase)
    Set<String> nombresNormalizados = extraerNombresNormalizados(hechos);

    // Cargar categorías existentes desde BD para reutilizar instancias
    Map<String, Categoria> categoriasExistentes = cargarCategoriasExistentes(nombresNormalizados);

    // Asignar categorías a hechos: reutilizar existentes o crear nuevas
    // Retorna mapa de categorías nuevas detectadas
    Map<String, Categoria> categoriasNuevas = asignarCategoriasAHechos(hechos, categoriasExistentes);

    // Persistir nuevas categorías en batch y actualizar referencias en hechos
    if (!categoriasNuevas.isEmpty()) {
      persistirNuevasCategorias(hechos, categoriasNuevas, categoriasExistentes);
    }
  }

  private Set<String> extraerNombresNormalizados(List<Hecho> hechos) {
    return hechos.stream()
        .map(Hecho::getCategoria)
        .filter(Objects::nonNull)
        .map(Categoria::getNombre)
        .filter(Objects::nonNull)
        .map(Categoria::normalizar)
        .collect(Collectors.toSet());
  }

  private Map<String, Categoria> cargarCategoriasExistentes(Set<String> nombresNormalizados) {
    Map<String, Categoria> categoriasMap = new HashMap<>();

    if (nombresNormalizados.isEmpty()) {
      return categoriasMap;
    }

    List<Categoria> existentes = categoriaRepository.findByNombreIn(new ArrayList<>(nombresNormalizados));
    existentes.forEach(categoria -> {
      if (categoria.getNombre() != null) {
        categoriasMap.put(Categoria.normalizar(categoria.getNombre()), categoria);
      }
    });

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

      String nombreNormalizado = Categoria.normalizar(categoriaActual.getNombre());

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

    // Persistir en batch todas las categorías nuevas (más eficiente que una por una)
    List<Categoria> categoriasPersistidas = categoriaRepository.saveAll(
        new ArrayList<>(categoriasNuevas.values())
    );

    // Actualizar mapa de existentes con las recién persistidas (ahora tienen ID)
    categoriasPersistidas.forEach(categoria ->
        categoriasExistentes.put(Categoria.normalizar(categoria.getNombre()), categoria)
    );

    // Actualizar referencias en hechos: si la categoría no tiene ID, usar la instancia persistida
    // (saveAll puede devolver instancias distintas con ID asignado)
    for (Hecho hecho : hechos) {
      Categoria categoria = hecho.getCategoria();

      if (categoria != null && categoria.getId() == null) {
        String nombreNormalizado = Categoria.normalizar(categoria.getNombre());
        hecho.setCategoria(categoriasExistentes.get(nombreNormalizado));
      }
    }
  }

  public ResponseEntity<List<String>> getAllCategoriasNombres() {
    log.info("[CategoriasService] Obteniendo todas las categorías");

    List<String> nombres = categoriaRepository.findAll().stream()
        .map(Categoria::getNombre)
        .filter(Objects::nonNull)
        .sorted()
        .toList();

    log.debug("[CategoriasService] Se encontraron {} categorías", nombres.size());
    return ResponseEntity.ok(nombres);
  }
}
