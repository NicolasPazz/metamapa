package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.fuente.impl.FuenteTiempoReal;
import ar.utn.ba.ddsi.agregador.domain.hecho.EstadoHecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.OrigenHecho;
import ar.utn.ba.ddsi.agregador.domain.ubicacion.Provincia;
import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.dto.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.dto.FuenteDTO;
import ar.utn.ba.ddsi.agregador.dto.mapper.FuenteMapper;
import ar.utn.ba.ddsi.agregador.domain.fuente.AFuente;
import ar.utn.ba.ddsi.agregador.repositorie.IFuentesRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuentesService {

  private final List<AFuente> fuentes = new ArrayList<>();
  private final SolicitudesService solicitudesService;
  private final IFuentesRepository fuentesRepository;
  private final FuenteMapper fuenteMapper;
  private final ProvinciaService provinciaService;

  public void agregarFuente(AFuente fuente) {
    if (!fuentes.contains(fuente)) {
      fuentes.add(fuente);
    }
  }

  public ResponseEntity<List<HechoDTO>> getAllHechosVisibles(
      String categoria,
      LocalDate fechaReporteDesde, LocalDate fechaReporteHasta,
      LocalDateTime fechaAcontecimientoDesde, LocalDateTime fechaAcontecimientoHasta,
      String ubicacion) {

    List<HechoDTO> resultados = new ArrayList<>();

    for (AFuente fuente : fuentes) {
      Long idFuente = fuente.getId();

      List<HechoDTO> hechos = Optional.ofNullable(
          fuente.getAllHechosVisibles(
              categoria,
              fechaReporteDesde, fechaReporteHasta,
              fechaAcontecimientoDesde, fechaAcontecimientoHasta,
              ubicacion
          ).getBody()
      ).orElse(List.of());

      hechos.forEach(h -> normalizarHecho(h, idFuente));

      resultados.addAll(hechos);
    }

    log.info("[FuentesService] {} hechos visibles combinados desde todas las fuentes.",
        resultados.size());

    return ResponseEntity.ok(filtrarBlacklist(resultados));
  }

  private List<HechoDTO> filtrarBlacklist(List<HechoDTO> hechos) {

    Map<Long, Set<Long>> blacklist = solicitudesService.blacklist();

    if (blacklist.isEmpty()) {
      log.info("[FuentesService] No hay elementos en la blacklist, no se filtra nada.");
      return hechos; // no hay nada que filtrar
    }

    return hechos.stream()
        .filter(h -> {
          if (h.getFuenteId() == null || h.getIdOrigen() == null) {
            return true; // mantener hechos sin fuenteId o idOrigen
          }
          Set<Long> idsProhibidos = blacklist.get(h.getFuenteId());
          return idsProhibidos == null || !idsProhibidos.contains(h.getIdOrigen());
        })
        .collect(Collectors.toList());
  }

  public ResponseEntity<List<HechoDTO>> getAllHechosNuevos() {
    List<HechoDTO> resultados = new ArrayList<>();

    for (AFuente fuente : fuentes) {
      Long idFuente = fuente.getId();

      List<HechoDTO> hechos = Optional.ofNullable(
          fuente.getAllHechosNuevos().getBody()
      ).orElse(List.of());

      hechos.forEach(h -> normalizarHecho(h, idFuente));

      resultados.addAll(hechos);
    }

    return ResponseEntity.ok(resultados);
  }


  public ResponseEntity<List<ColeccionDTO>> getAllColecciones() {
    log.info("📡 [FuentesService] Iniciando consulta de colecciones desde {} fuentes", fuentes.size());
    List<ColeccionDTO> resultados = new ArrayList<>();

    for (AFuente fuente : fuentes) {
      Long idFuente = fuente.getId();
      log.info("📡 [FuentesService] Consultando colecciones de fuente ID={}, Nombre={}", idFuente, fuente.getNombre());

      List<ColeccionDTO> colecciones = Optional.ofNullable(
          fuente.getAllColecciones().getBody()
      ).orElse(List.of());

      log.info("📡 [FuentesService] Fuente {} devolvió {} colecciones", fuente.getNombre(), colecciones.size());

      colecciones.forEach(c -> normalizarColeccion(c, idFuente));

      resultados.addAll(colecciones);
    }

    log.info("📡 [FuentesService] Total de colecciones obtenidas: {}", resultados.size());
    return ResponseEntity.ok(resultados);
  }


  public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
      UUID handle,
      String categoria,
      LocalDate fechaReporteDesde, LocalDate fechaReporteHasta,
      LocalDateTime fechaAcontecimientoDesde, LocalDateTime fechaAcontecimientoHasta,
      String ubicacion,
      String modo
  ) {

    for (AFuente fuente : fuentes) {
      Long idFuente = fuente.getId();

      ResponseEntity<List<HechoDTO>> respuesta = fuente.getHechosColeccionByHandle(
          handle,
          categoria,
          fechaReporteDesde, fechaReporteHasta,
          fechaAcontecimientoDesde, fechaAcontecimientoHasta,
          ubicacion,
          modo
      );

      List<HechoDTO> hechos = Optional.ofNullable(respuesta.getBody()).orElse(List.of());

      hechos.forEach(h -> normalizarHecho(h, idFuente));

      return ResponseEntity.ok(hechos);
    }

    return ResponseEntity.notFound().build();
  }

  public ResponseEntity<List<FuenteDTO>> getAllFuentes() {
    log.info("[FuentesService] Obteniendo todas las fuentes desde BD");
    List<AFuente> fuentesPersistidas = fuentesRepository.findAll();
    List<FuenteDTO> dtos = fuenteMapper.toDTO(fuentesPersistidas);
    log.info("[FuentesService] Se encontraron {} fuentes", dtos.size());
    return ResponseEntity.ok(dtos);
  }

  public AFuente getFuenteById(Long idFuente) {
    return fuentes.stream()
        .filter(fuente -> fuente.getId().equals(idFuente))
        .findFirst()
        .orElse(null);
  }

  public int cantidadDeFuentes() {
    return fuentes.size();
  }

  public List<Long> getFuentesTiempoReal() {
    return fuentes.stream()
        .filter(fuente -> fuente instanceof FuenteTiempoReal)
        .map(AFuente::getId)
        .collect(Collectors.toList());
  }

  public List<OrigenHecho> getOrigenesTiempoReal() {
    return List.of(OrigenHecho.METAMAPA);
  }

  public List<String> getNombresFuentesTiempoReal() {
    return fuentes.stream()
        .filter(fuente -> fuente instanceof FuenteTiempoReal)
        .map(AFuente::getNombre)
        .collect(Collectors.toList());
  }


  private void normalizarHecho(HechoDTO h, Long idFuente) {
    if (h.getFechaDeAcontecimiento() == null) {
      log.warn("DTO idOrigen {} sin fecha_acontecimiento recibido desde fuente {}", h.getId(), idFuente);
    }
    if (h.getId() == null) {
      log.warn("Hecho sin id recibido desde fuente {}", idFuente);
    } else {
      h.setIdOrigen(h.getId());
      h.setId(null);
    }

    h.setFuenteId(idFuente);
    
    // Asignar nombre de la fuente desde la configuración
    AFuente fuente = getFuenteById(idFuente);
    if (fuente != null) {
      h.setNombreFuente(fuente.getNombre());
    }

    // Calcular y asignar provincia si no la tiene
    if (h.getProvincia() == null && h.getLatitud() != null && h.getLongitud() != null) {
      Provincia provincia =
          provinciaService.obtenerProvinciaPorCoordenadas(h.getLatitud(), h.getLongitud());
      h.setProvincia(provincia.getNombre());
      log.debug("[FuentesService] Provincia calculada para hecho externo: {}", provincia.getNombre());
    }

    if (h.getEstado() == null) {
      h.setEstado(EstadoHecho.VISIBLE);
      log.debug("Aplicado estado default VISIBLE al hecho idOrigen {} de fuente {}", h.getIdOrigen(), idFuente);
    }

    if (h.getEtiquetas() == null) {
      h.setEtiquetas(new ArrayList<>());
    }

    if (h.getMultimedia() == null) {
      h.setMultimedia(new ArrayList<>());
    }

    if (h.getTransiciones() == null) {
      h.setTransiciones(new ArrayList<>());
    }
  }

  private void normalizarColeccion(ColeccionDTO c, Long idFuente) {
    if (c.getHandle() == null) {
      log.warn("Colección sin handle recibida desde fuente {}", idFuente);
    } else {
      c.setIdOrigen(c.getHandle());
    }
    c.setFuenteId(idFuente);

    // Aplicar defaults para atributos que otras fuentes pueden no proveer
    if (c.getCriteriosDePertenencia() == null) {
      log.debug("Colección de fuente {} sin criterio de pertenencia", idFuente);
    }

    // El algoritmoConsenso puede ser null si viene de fuentes externas
    if (c.getAlgoritmoConsenso() == null) {
      log.debug("Colección de fuente {} sin algoritmo de consenso definido", idFuente);
    }

    if (c.getHechos() == null) {
      c.setHechos(new ArrayList<>());
    } else {
      // Normalizar cada hecho dentro de la colección
      c.getHechos().forEach(h -> normalizarHecho(h, idFuente));
    }

    if (c.getHechosConsensuados() == null) {
      c.setHechosConsensuados(new ArrayList<>());
    } else {
      // Normalizar cada hecho consensuado dentro de la colección
      c.getHechosConsensuados().forEach(h -> normalizarHecho(h, idFuente));
    }
  }
}
