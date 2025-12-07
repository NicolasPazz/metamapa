package ar.utn.ba.ddsi.agregador.service.impl;

import ar.utn.ba.ddsi.agregador.domain.hecho.Categoria;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.repositorie.ICategoriaRepository;
import ar.utn.ba.ddsi.agregador.service.ICategoriasService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoriasService implements ICategoriasService {

  private final ICategoriaRepository categoriaRepository;

  public void persistirCategoriasDesdeHechos(List<Hecho> hechosNuevos) {
    // 1) extraer nombres normalizados de categoría
    Set<String> nombres = hechosNuevos.stream()
        .map(h -> {
          Categoria c = h.getCategoria();
          return c == null ? null : c.getNombre() == null ? null : c.getNombre().trim().toLowerCase();
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    // 2) buscar categorías existentes en DB
    Map<String, Categoria> cache = new HashMap<>();
    if (!nombres.isEmpty()) {
      List<Categoria> existentes = categoriaRepository.findByNombreIn(new ArrayList<>(nombres));
      existentes.forEach(c -> cache.put(c.getNombre().trim().toLowerCase(), c));
    }

    // 3) para cada hecho, asignar la categoría correcta (reusar existente o crear nueva en memoria)
    Map<String, Categoria> nuevasPorNombre = new HashMap<>();
    for (Hecho h : hechosNuevos) {
      Categoria c = h.getCategoria();
      if (c == null) continue;
      String nombreNorm = c.getNombre() == null ? null : c.getNombre().trim().toLowerCase();
      if (nombreNorm == null) continue;

      // si ya está en cache (existente o creada anteriormente), reusar
      if (cache.containsKey(nombreNorm)) {
        h.setCategoria(cache.get(nombreNorm));
        continue;
      }
      if (nuevasPorNombre.containsKey(nombreNorm)) {
        h.setCategoria(nuevasPorNombre.get(nombreNorm));
        continue;
      }

      // crear nueva Categoria (no persistida aún), poner en nuevasPorNombre y asignar
      Categoria nueva = new Categoria(nombreNorm);
      nuevasPorNombre.put(nombreNorm, nueva);
      h.setCategoria(nueva);
    }

    // 4) persistir las categorías nuevas en batch (si hay)
    if (!nuevasPorNombre.isEmpty()) {
      List<Categoria> saved = categoriaRepository.saveAll(new ArrayList<>(nuevasPorNombre.values()));
      // actualizar cache con las guardadas (con id)
      saved.forEach(c -> cache.put(c.getNombre().trim().toLowerCase(), c));
      // también actualizar las referencias en hechos (por si saveAll devolvió instancias distintas)
      for (Hecho h : hechosNuevos) {
        if (h.getCategoria() != null && h.getCategoria().getId() == null) {
          String nn = h.getCategoria().getNombre().trim().toLowerCase();
          h.setCategoria(cache.get(nn));
        }
      }
    }
  }
}
