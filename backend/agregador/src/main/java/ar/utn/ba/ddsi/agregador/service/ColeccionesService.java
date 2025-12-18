package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.coleccion.ModoNavegacion;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.TipoAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.impl.Absoluta;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.impl.MayoriaSimple;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.impl.MultiplesMenciones;
import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.Criterio;
import ar.utn.ba.ddsi.agregador.dto.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.dto.CriterioDTO;
import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.dto.PageResponse;
import ar.utn.ba.ddsi.agregador.dto.mapper.ColeccionMapper;
import ar.utn.ba.ddsi.agregador.dto.mapper.CriterioMapper;
import ar.utn.ba.ddsi.agregador.dto.mapper.HechoMapper;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.domain.hecho.OrigenHecho;
import ar.utn.ba.ddsi.agregador.repositorie.IColeccionesRepository;
import ar.utn.ba.ddsi.agregador.repositorie.IHechosRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ColeccionesService {

    private final IColeccionesRepository coleccionesRepository;
    private final IHechosRepository hechosRepository;
    private final FuentesService fuentesService;
    private final HechoMapper hechoMapper;
    private final ColeccionMapper coleccionMapper;
    private final CriterioMapper criterioMapper;
    private final CategoriasService categoriasService;
    private final ObjectMapper objectMapper;

    public ResponseEntity<ColeccionDTO> getOneColeccion() {
        // Intentar obtener de la BD local
        List<Coleccion> coleccionesLocales = coleccionesRepository.findAll();

        if (!coleccionesLocales.isEmpty()) {
            ColeccionDTO coleccionDTO = coleccionMapper.toDTO(coleccionesLocales.get(0));
            return ResponseEntity.ok(coleccionDTO);
        }

        // Si no hay locales, intentar obtener de fuentes externas
        try {
            ResponseEntity<List<ColeccionDTO>> respuestaExterna = fuentesService.getAllColecciones();
            List<ColeccionDTO> coleccionesExternas = Optional.ofNullable(respuestaExterna.getBody())
                    .orElse(List.of());

            if (!coleccionesExternas.isEmpty()) {
                ColeccionDTO coleccionExterna = coleccionesExternas.get(0);
                return ResponseEntity.ok(coleccionExterna);
            }
        } catch (Exception e) {
            log.warn("[ColeccionesService] Error al obtener colecciones de fuentes externas en getOneColeccion: {}", e.getMessage());
        }

        log.warn("[ColeccionesService] No hay colecciones disponibles ni en BD local ni en fuentes externas");
        return ResponseEntity.notFound().build();
    }

    @SuppressWarnings("unused")
    public ResponseEntity<List<ColeccionDTO>> getAllColeccionesPaginated(int page, int size) {

        List<ColeccionDTO> coleccionesLocales = Optional.ofNullable(
            getAllColeccionesLocales().getBody()
        ).orElse(new ArrayList<>());

        List<ColeccionDTO> coleccionesFuentes = new ArrayList<>();
        try {
            coleccionesFuentes = Optional.ofNullable(
                fuentesService.getAllColecciones().getBody()
            ).orElse(List.of());
        } catch (Exception e) {
            log.warn("[ColeccionesService] Error al obtener colecciones externas en getPaginatedColecciones: {}", e.getMessage());
        }

        List<ColeccionDTO> todasLasColecciones = new ArrayList<>();
        todasLasColecciones.addAll(coleccionesLocales);
        todasLasColecciones.addAll(coleccionesFuentes);

        // Paginar
        int start = page * size;
        int end = Math.min(start + size, todasLasColecciones.size());

        if (start >= todasLasColecciones.size()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<ColeccionDTO> paginadas = todasLasColecciones.subList(start, end);

        return ResponseEntity.ok(paginadas);
    }

    public ResponseEntity<PageResponse<HechoDTO>> getHechosColeccionPaginated(
            String handle,
            String ubicacion,
            LocalDate fechaReporteDesde,
            LocalDate fechaReporteHasta,
            LocalDateTime fechaAcontecimientoDesde,
            LocalDateTime fechaAcontecimientoHasta,
            String categoria,
            String fuente,
            String modoStr,
            int page,
            int size) {


        // Parsear modo de navegación
        ModoNavegacion modo = null;
        if (modoStr != null && !modoStr.trim().isEmpty()) {
            try {
                modo = ModoNavegacion.valueOf(modoStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("[ColeccionesService] Modo inválido: {}, usando IRRESTRICTA por defecto", modoStr);
                modo = ModoNavegacion.IRRESTRICTA;
            }
        }
      
        ResponseEntity<List<HechoDTO>> respuesta = getHechosColeccionByHandle(
            handle, categoria, fechaReporteDesde, fechaReporteHasta, 
            fechaAcontecimientoDesde, fechaAcontecimientoHasta, ubicacion, fuente, modo);

        if (respuesta.getStatusCode().isError() || respuesta.getBody() == null) {
            PageResponse<HechoDTO> emptyResponse = new PageResponse<>(
                    new ArrayList<>(),
                    page,
                    size,
                    false,
                    page > 0,
                    0
            );
            return ResponseEntity.ok(emptyResponse);
        }

        List<HechoDTO> todosLosHechos = respuesta.getBody();
        long total = todosLosHechos.size();

        // Paginar
        int start = page * size;
        int end = Math.min(start + size, todosLosHechos.size());

        if (start >= todosLosHechos.size()) {
            PageResponse<HechoDTO> emptyResponse = new PageResponse<>(
                    new ArrayList<>(),
                    page,
                    size,
                    false,
                    page > 0,
                    total
            );
            return ResponseEntity.ok(emptyResponse);
        }

        List<HechoDTO> paginados = todosLosHechos.subList(start, end);

        // Determinar si hay página siguiente
        boolean hasNext = end < todosLosHechos.size();

        PageResponse<HechoDTO> response = new PageResponse<>(
                paginados,
                page,
                size,
                hasNext,
                page > 0,
                total
        );


        return ResponseEntity.ok(response);
    }

    public ResponseEntity<List<ColeccionDTO>> getAllColecciones() {

        List<ColeccionDTO> coleccionesLocales = Optional.ofNullable(
            getAllColeccionesLocales().getBody()
        ).orElse(new ArrayList<>());

        List<ColeccionDTO> coleccionesFuentes = new ArrayList<>();
        try {
            coleccionesFuentes = Optional.ofNullable(
                fuentesService.getAllColecciones().getBody()
            ).orElse(List.of());
            } catch (Exception e) {
                log.warn("[ColeccionesService] Error al obtener colecciones de fuentes externas en getAllColecciones: {}", e.getMessage());
            }

        List<ColeccionDTO> todasLasColecciones = new ArrayList<>();
        todasLasColecciones.addAll(coleccionesLocales);
        todasLasColecciones.addAll(coleccionesFuentes);

        return ResponseEntity.ok(todasLasColecciones);
    }

    public ResponseEntity<List<ColeccionDTO>> getAllColeccionesLocales() {

        List<Coleccion> coleccionesBD = coleccionesRepository.findAll();
        List<ColeccionDTO> dtosBD = coleccionesBD.stream()
            .map(coleccionMapper::toDTO)
            .toList();

        return ResponseEntity.ok(dtosBD);
    }

    public ResponseEntity<List<ColeccionDTO>> getAllColeccionesExternas() {

        List<ColeccionDTO> coleccionesExternas = new ArrayList<>();
        try {
            coleccionesExternas = Optional.ofNullable(
                fuentesService.getAllColecciones().getBody()
            ).orElse(List.of());
            log.info("[ColeccionesService] {} colecciones externas obtenidas en getAllColeccionesExternas", coleccionesExternas.size());
        } catch (Exception e) {
            log.warn("[ColeccionesService] Error al obtener colecciones externas en getAllColeccionesExternas: {}", e.getMessage());
        }

        return ResponseEntity.ok(coleccionesExternas);
    }

    public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
        String handle,
        String categoria,
        LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
        LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
        String ubicacion,
        String fuente,
        ModoNavegacion modo) {

        log.info("[ColeccionesService] Obteniendo hechos de colección: handle={}, modo={}", handle, modo);

        try {
            UUID uuid = UUID.fromString(handle);

            // Primero verificar si es una colección local (en BD)
            Optional<Coleccion> coleccionOpt = coleccionesRepository.findById(uuid);

            // Si la colección está en BD, usar sus listas persistidas
            if (coleccionOpt.isPresent()) {
                log.info("[ColeccionesService] Colección local encontrada en BD");
                Coleccion coleccion = coleccionOpt.get();

                // Si no hay criterios, retornar lista vacía sin buscar hechos
                if (coleccion.getCriteriosDePertenencia() == null || coleccion.getCriteriosDePertenencia().isEmpty()) {
                    log.info("[ColeccionesService] Colección sin criterios, retornando lista vacía");
                    return ResponseEntity.ok(new ArrayList<>());
                }

                // 1. Obtener hechos según el modo de navegación
                ModoNavegacion modoNav = modo != null ? modo : ModoNavegacion.IRRESTRICTA;
                List<Hecho> hechos;

                if (modoNav == ModoNavegacion.CURADA) {
                    // Modo CURADA: usar hechosConsensuados persistidos (solo VISIBLES)
                    hechos = new ArrayList<>(coleccion.getHechosConsensuados().stream()
                        .filter(Hecho::esVisible)
                        .toList());
                    log.info("[ColeccionesService] Modo CURADA: usando {} hechos consensuados VISIBLES persistidos", hechos.size());
                } else {
                    // Modo IRRESTRICTA: usar hechos persistidos (solo VISIBLES)
                    hechos = new ArrayList<>(coleccion.getHechos().stream()
                        .filter(Hecho::esVisible)
                        .toList());
                    log.info("[ColeccionesService] Modo IRRESTRICTA: usando {} hechos VISIBLES persistidos", hechos.size());
                }

                // 2. Si la colección tiene hechos externos, agregar los de tiempo real
                List<Long> fuentesTiempoReal = fuentesService.getFuentesTiempoReal();
                List<OrigenHecho> origenesTiempoReal = fuentesService.getOrigenesTiempoReal();

                if (coleccion.tieneHechosExternos(fuentesTiempoReal, origenesTiempoReal, fuentesService.getNombresFuentesTiempoReal())) {
                    log.info("[ColeccionesService] Colección tiene hechos externos, obteniendo de fuentes en tiempo real");
                    try {
                        ResponseEntity<List<HechoDTO>> respuestaExternos = fuentesService.getAllHechosVisibles(
                            null, null, null, null, null, null);
                        List<HechoDTO> hechosExternosDTO = Optional.ofNullable(respuestaExternos.getBody()).orElse(List.of());
                        List<Hecho> hechosExternos = hechoMapper.fromDTO(hechosExternosDTO);

                        // Aplicar criterios de pertenencia a los hechos externos
                        List<Hecho> hechosExternosFiltrados = coleccion.aplicarCriteriosDePertenencia(hechosExternos);
                        log.info("[ColeccionesService] Hechos externos obtenidos y filtrados: {}", hechosExternosFiltrados.size());

                        // Si es modo CURADA, aplicar algoritmo de consenso a los externos también
                        if (modoNav == ModoNavegacion.CURADA && coleccion.getAlgoritmoConsenso() != null) {
                            int cantidadFuentes = calcularCantidadFuentes();
                            List<Hecho> todosHechos = new ArrayList<>(hechos);
                            todosHechos.addAll(hechosExternosFiltrados);
                            List<Hecho> hechosPersistidos = hechos; // Variable final para lambda
                            hechosExternosFiltrados = coleccion.aplicarAlgoritmoConsenso(todosHechos, cantidadFuentes)
                                .stream()
                                .filter(h -> !hechosPersistidos.contains(h)) // Solo los nuevos
                                .toList();
                            log.info("[ColeccionesService] Hechos externos consensuados: {}", hechosExternosFiltrados.size());
                        }

                        hechos.addAll(hechosExternosFiltrados);
                        log.info("[ColeccionesService] Total con externos: {}", hechos.size());
                    } catch (Exception e) {
                        log.warn("[ColeccionesService] Error al obtener hechos externos en getHechosColeccionByHandle (local): {}", e.getMessage());
                    }
                } else {
                    log.info("[ColeccionesService] Colección no tiene hechos externos, usando solo persistidos");
                }

                // 3. Aplicar filtros adicionales (parámetros de query)
                log.info("[ColeccionesService] Aplicando filtros adicionales");
                hechos = aplicarFiltros(hechos, categoria,
                    fecha_reporte_desde, fecha_reporte_hasta,
                    fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion, fuente);
                log.info("[ColeccionesService] Después de filtros: {} hechos", hechos.size());

                List<HechoDTO> hechosDTO = hechoMapper.toDTO(hechos);
                log.info("[ColeccionesService] Retornando {} hechos de la colección {}", hechosDTO.size(), handle);
                return ResponseEntity.ok(hechosDTO);
            }

            // Si no está en BD, verificar si es una colección externa (resiliente a fallos)
            try {
                List<ColeccionDTO> coleccionesExternas = Optional.ofNullable(
                    fuentesService.getAllColecciones().getBody()
                ).orElse(List.of());

                // Buscar la colección por handle en las externas
                Optional<ColeccionDTO> coleccionExterna = coleccionesExternas.stream()
                    .filter(c -> uuid.equals(c.getHandle()))
                    .findFirst();

                if (coleccionExterna.isPresent()) {
                    ColeccionDTO coleccion = coleccionExterna.get();
                    log.info("[ColeccionesService] Colección externa encontrada: {} (fuente: {})",
                        coleccion.getTitulo(), coleccion.getFuenteId());

                    // Seleccionar hechos según modo de navegación
                    ModoNavegacion modoNav = modo != null ? modo : ModoNavegacion.IRRESTRICTA;
                    List<HechoDTO> hechos;

                    if (modoNav == ModoNavegacion.CURADA && coleccion.getHechosConsensuados() != null) {
                        hechos = new ArrayList<>(coleccion.getHechosConsensuados());
                        log.info("[ColeccionesService] Modo CURADA: usando {} hechos consensuados de colección externa", hechos.size());
                    } else if (coleccion.getHechos() != null) {
                        hechos = new ArrayList<>(coleccion.getHechos());
                        log.info("[ColeccionesService] Modo IRRESTRICTA: usando {} hechos de colección externa", hechos.size());
                    } else {
                        hechos = new ArrayList<>();
                        log.warn("[ColeccionesService] Colección externa no tiene hechos");
                    }

                    // Aplicar filtros adicionales a los hechos del DTO
                    List<Hecho> hechosEntidad = hechoMapper.fromDTO(hechos);
                    hechosEntidad = aplicarFiltros(hechosEntidad, categoria,
                        fecha_reporte_desde, fecha_reporte_hasta,
                        fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion, fuente);

                    List<HechoDTO> hechosDTO = hechoMapper.toDTO(hechosEntidad);
                    log.info("[ColeccionesService] Retornando {} hechos de colección externa {}", hechosDTO.size(), handle);
                    return ResponseEntity.ok(hechosDTO);
                }
            } catch (Exception e) {
                log.warn("[ColeccionesService] No se pudieron consultar fuentes externas para buscar colección {}: {}",
                    handle, e.getMessage());
            }

            // Si no se encuentra ni en BD ni en fuentes externas (o si las fuentes están caídas)
            log.warn("[ColeccionesService] Colección no encontrada en getHechosColeccionByHandle: {}", handle);
            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            log.warn("[ColeccionesService] Handle inválido en getHechosColeccionByHandle: {}", handle);
            return ResponseEntity.badRequest().build();
        }
    }

    @Transactional
    public ResponseEntity<?> crearColeccion(ColeccionDTO dto) {
        if (dto == null || dto.getTitulo() == null || dto.getDescripcion() == null) {
            log.warn("[ColeccionesService] Intento de crear colección con datos incompletos");
            return ResponseEntity.badRequest().body("Título y descripción son obligatorios");
        }

        try {
            log.info("[ColeccionesService] Creando colección: {}", dto.getTitulo());

            List<Criterio> criterios = dto.getCriteriosDePertenencia() != null && !dto.getCriteriosDePertenencia().isEmpty()
                ? new ArrayList<>(dto.getCriteriosDePertenencia().stream().map(criterioMapper::fromDTO).toList())
                : new ArrayList<>();

            AAlgoritmoConsenso algoritmo = dto.getAlgoritmoConsenso() != null
                ? crearAlgoritmoConsenso(dto.getAlgoritmoConsenso())
                : null;

            Coleccion coleccionTemporal = Coleccion.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .algoritmoConsenso(algoritmo)
                .criteriosDePertenencia(criterios)
                .build();

            List<Hecho> todosHechosFiltrados;
            List<Hecho> todosHechosConsensuados;

            // Si no hay criterios, no buscar hechos y usar listas vacías
            if (criterios == null || criterios.isEmpty()) {
                log.info("[ColeccionesService] Colección sin criterios, no se buscarán hechos");
                todosHechosFiltrados = new ArrayList<>();
                todosHechosConsensuados = new ArrayList<>();
            } else {
                List<Hecho> hechosLocalesVisibles = hechosRepository.findAll().stream()
                    .filter(Hecho::esVisible)
                    .toList();

                List<Hecho> todosHechos = new ArrayList<>(hechosLocalesVisibles);

                if (coleccionTemporal.tieneHechosExternos(fuentesService.getFuentesTiempoReal(), fuentesService.getOrigenesTiempoReal(), fuentesService.getNombresFuentesTiempoReal())) {
                try {
                    ResponseEntity<List<HechoDTO>> respuestaExternos = fuentesService.getAllHechosVisibles(
                        null, null, null, null, null, null);
                    List<HechoDTO> hechosExternosDTO = Optional.ofNullable(respuestaExternos.getBody()).orElse(List.of());
                    List<Hecho> hechosExternos = hechoMapper.fromDTO(hechosExternosDTO);

                    if (!hechosExternos.isEmpty()) {
                        categoriasService.persistirCategoriasDesdeHechos(hechosExternos);
                        todosHechos.addAll(hechosExternos);
                    }
                } catch (Exception e) {
                    log.warn("[ColeccionesService] Error al obtener hechos externos en getHechosColeccionByHandle (externo): {}", e.getMessage());
                }
                }

                todosHechosFiltrados = coleccionTemporal.aplicarCriteriosDePertenencia(todosHechos);
                todosHechosConsensuados = coleccionTemporal.aplicarAlgoritmoConsenso(todosHechosFiltrados, calcularCantidadFuentes());
            }

            List<Hecho> hechosLocalesFiltrados = todosHechosFiltrados.stream()
                .filter(h -> h.getId() != null)
                .toList();

            List<Hecho> hechosLocalesConsensuados = todosHechosConsensuados.stream()
                .filter(h -> h.getId() != null)
                .toList();

            Coleccion coleccion = Coleccion.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .algoritmoConsenso(algoritmo)
                .criteriosDePertenencia(criterios)
                .hechos(hechosLocalesFiltrados)
                .hechosConsensuados(hechosLocalesConsensuados)
                .build();

            Coleccion coleccionGuardada = coleccionesRepository.saveAndFlush(coleccion);

            ColeccionDTO respuesta = coleccionMapper.toDTO(coleccionGuardada);
            respuesta.setHechos(hechoMapper.toDTO(todosHechosFiltrados));
            respuesta.setHechosConsensuados(hechoMapper.toDTO(todosHechosConsensuados));

            log.info("[ColeccionesService] Colección creada con handle: {} ({} hechos totales: {} locales + {} externos, {} consensuados)",
                coleccionGuardada.getHandle(), todosHechosFiltrados.size(),
                hechosLocalesFiltrados.size(), todosHechosFiltrados.size() - hechosLocalesFiltrados.size(),
                todosHechosConsensuados.size());

            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);

        } catch (Exception e) {
            log.error("[ColeccionesService] Error al crear colección: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear la colección: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> actualizarColeccion(String handle, JsonNode body) {
        try {
            log.info("[ColeccionesService] Actualizando colección {}", handle);
            UUID uuid = UUID.fromString(handle);
            Optional<Coleccion> coleccionOpt = coleccionesRepository.findById(uuid);

            if (coleccionOpt.isEmpty()) {
                log.warn("[ColeccionesService] Colección no encontrada: {}", handle);
                return ResponseEntity.notFound().build();
            }

            Coleccion coleccion = coleccionOpt.get();
            boolean cambiaronCriterios = false;
            boolean cambioAlgoritmo = false;

            if (body.has("titulo") && !body.get("titulo").isNull()) {
              String titulo = body.get("titulo").asText().trim();
              if (!titulo.isBlank()) {
                coleccion.setTitulo(titulo);
              }
            }

            if (body.has("descripcion") && !body.get("descripcion").isNull()) {
              String descripcion = body.get("descripcion").asText().trim();
              if (!descripcion.isBlank()) {
                coleccion.setDescripcion(descripcion);
              }
            }

            if (body.has("criterios")) {
              JsonNode criteriosNode = body.get("criterios");

              if (criteriosNode.isNull()
                  || (criteriosNode.isArray() && criteriosNode.isEmpty())) {

                coleccion.setCriteriosDePertenencia(new ArrayList<>());
                log.info("[ColeccionesService] Criterios eliminados (null o vacío)");

              } else if (criteriosNode.isArray()) {

                try {
                  CriterioDTO[] criteriosArray =
                      objectMapper.treeToValue(criteriosNode, CriterioDTO[].class);

                  List<Criterio> nuevos = Arrays.stream(criteriosArray)
                      .map(criterioMapper::fromDTO)
                      .toList();

                  coleccion.setCriteriosDePertenencia(new ArrayList<>(nuevos));
                  log.info("[ColeccionesService] Criterios actualizados ({} criterios)", nuevos.size());

                } catch (Exception e) {
                  log.error("[ColeccionesService] Error al parsear criterios", e);
                  return ResponseEntity.badRequest()
                      .body("Error al parsear criterios");
                }

              } else {
                return ResponseEntity.badRequest()
                    .body("El campo 'criterios' debe ser un array");
              }

              cambiaronCriterios = true;
            }

            if (body.has("algoritmo_consenso")) {
              JsonNode algoritmoNode = body.get("algoritmo_consenso");

              if (algoritmoNode.isNull()) {
                coleccion.setAlgoritmoConsenso(null);
                log.info("[ColeccionesService] Algoritmo eliminado (null = sin consenso)");

              } else {
                try {
                  String algoritmoStr = algoritmoNode.asText();
                  TipoAlgoritmoConsenso tipo =
                      TipoAlgoritmoConsenso.valueOf(algoritmoStr);

                  AAlgoritmoConsenso algoritmo = crearAlgoritmoConsenso(tipo);
                  coleccion.setAlgoritmoConsenso(algoritmo);

                  log.info("[ColeccionesService] Algoritmo actualizado a {}", tipo);

                } catch (IllegalArgumentException e) {
                  return ResponseEntity.badRequest()
                      .body("Algoritmo inválido. Valores posibles: "
                          + Arrays.toString(TipoAlgoritmoConsenso.values()));
                }
              }

              cambioAlgoritmo = true;
            }

            if (cambiaronCriterios || cambioAlgoritmo) {
                if (cambiaronCriterios) {
                    log.info("[ColeccionesService] Recalculando hechos y hechos consensuados (cambiaron criterios)");
                    recalcularHechosYConsenso(coleccion);
                } else {
                    log.info("[ColeccionesService] Recalculando solo hechos consensuados (cambió algoritmo)");
                    recalcularSoloConsenso(coleccion);
                }
            }

            Coleccion coleccionActualizada = coleccionesRepository.save(coleccion);
            log.info("[ColeccionesService] Colección {} actualizada exitosamente", handle);

            return ResponseEntity.ok(coleccionMapper.toDTO(coleccionActualizada));

        } catch (IllegalArgumentException e) {
            log.warn("[ColeccionesService] Handle inválido: {}", handle);
            return ResponseEntity.badRequest().body("Handle inválido");
        } catch (Exception e) {
            log.error("[ColeccionesService] Error al actualizar colección: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar la colección");
        }
    }

    private void recalcularHechosYConsenso(Coleccion coleccion) {
        // Si no hay criterios, establecer listas vacías sin buscar hechos
        if (coleccion.getCriteriosDePertenencia() == null || coleccion.getCriteriosDePertenencia().isEmpty()) {
            log.info("[ColeccionesService] Colección sin criterios, estableciendo listas vacías");
            coleccion.setHechos(new ArrayList<>());
            coleccion.setHechosConsensuados(new ArrayList<>());
            return;
        }

        List<Hecho> hechosLocalesVisibles = hechosRepository.findAll().stream()
            .filter(Hecho::esVisible)
            .toList();

        List<Hecho> hechosFiltrados = coleccion.aplicarCriteriosDePertenencia(hechosLocalesVisibles);
        coleccion.setHechos(new ArrayList<>(hechosFiltrados));

        List<Hecho> hechosConsensuados = coleccion.aplicarAlgoritmoConsenso(hechosFiltrados, calcularCantidadFuentes());
        coleccion.setHechosConsensuados(new ArrayList<>(hechosConsensuados));

        log.debug("[ColeccionesService] Hechos: {}, Consensuados: {}", hechosFiltrados.size(), hechosConsensuados.size());
    }

    private void recalcularSoloConsenso(Coleccion coleccion) {
        List<Hecho> hechos = new ArrayList<>(coleccion.getHechos());
        List<Hecho> hechosConsensuados = coleccion.aplicarAlgoritmoConsenso(hechos, calcularCantidadFuentes());
        coleccion.setHechosConsensuados(new ArrayList<>(hechosConsensuados));

        log.debug("[ColeccionesService] Consensuados: {}", hechosConsensuados.size());
    }

    @Transactional
    public ResponseEntity<?> eliminarColeccion(String handle) {
        try {
            UUID uuid = UUID.fromString(handle);
            Optional<Coleccion> coleccion = coleccionesRepository.findById(uuid);

            if (coleccion.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            coleccionesRepository.delete(coleccion.get());
            log.info("[ColeccionesService] Colección eliminada: {}", handle);
            return ResponseEntity.noContent().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Handle inválido");
        }
    }

    @Transactional
    public void actualizarHechosSegunConsenso() {
        log.info("[ColeccionesService] Actualizando hechos según consenso para todas las colecciones");
        List<Coleccion> colecciones = coleccionesRepository.findAll();
        final int cantidadFuentes = calcularCantidadFuentes();

        List<Hecho> hechosLocalesVisibles = hechosRepository.findAll().stream()
            .filter(Hecho::esVisible)
            .toList();

        for (Coleccion coleccion : colecciones) {
            coleccion.recalcularHechos(hechosLocalesVisibles, cantidadFuentes);
        }

        coleccionesRepository.saveAll(colecciones);
        log.info("[ColeccionesService] Actualización de consenso completada para {} colecciones", colecciones.size());
    }

    private AAlgoritmoConsenso crearAlgoritmoConsenso(TipoAlgoritmoConsenso tipo) {
        if (tipo == null) {
            log.warn("[ColeccionesService] Tipo de algoritmo null, usando MULTIPLES_MENCIONES por defecto");
            tipo = TipoAlgoritmoConsenso.MULTIPLES_MENCIONES;
        }

        return switch (tipo) {
            case ABSOLUTA -> Absoluta.getInstance();
            case MAYORIA_SIMPLE -> MayoriaSimple.getInstance();
            case MULTIPLES_MENCIONES -> MultiplesMenciones.getInstance();
        };
    }


    private List<Hecho> aplicarFiltros(List<Hecho> hechos, String categoria,
                                       LocalDate fechaReporteDesde, LocalDate fechaReporteHasta,
                                       LocalDateTime fechaAcontecimientoDesde, LocalDateTime fechaAcontecimientoHasta,
                                       String ubicacion,
                                       String fuente) {

        List<Hecho> resultado = hechos;
        int iniciales = resultado.size();

        // Filtro de categoría
        if (categoria != null && !categoria.trim().isEmpty()) {
            resultado = new ArrayList<>(resultado.stream()
                .filter(h -> h.getCategoria() != null &&
                    h.getCategoria().getNombre() != null &&
                    h.getCategoria().getNombre().toLowerCase().contains(categoria.toLowerCase()))
                .toList());
            log.debug("[aplicarFiltros] Categoría '{}': {} -> {} hechos", categoria, iniciales, resultado.size());
            iniciales = resultado.size();
        }

        // Filtro de ubicación (solo por provincia)
        if (ubicacion != null && !ubicacion.trim().isEmpty()) {
            String provinciaNombre = ubicacion.trim();
            resultado = new ArrayList<>(resultado.stream()
                .filter(h -> h.getProvincia() != null &&
                    h.getProvincia().getNombre() != null &&
                    h.getProvincia().getNombre().equalsIgnoreCase(provinciaNombre))
                .toList());
            log.debug("[aplicarFiltros] Provincia '{}': {} -> {} hechos", ubicacion, iniciales, resultado.size());
            iniciales = resultado.size();
        }

        // Filtro de fecha de reporte desde
        if (fechaReporteDesde != null) {
            resultado = new ArrayList<>(resultado.stream()
                .filter(h -> !h.getFechaDeCarga().toLocalDate().isBefore(fechaReporteDesde))
                .toList());
            log.debug("[aplicarFiltros] Fecha reporte desde {}: {} -> {} hechos",
                fechaReporteDesde, iniciales, resultado.size());
            iniciales = resultado.size();
        }

        // Filtro de fecha de reporte hasta
        if (fechaReporteHasta != null) {
            resultado = new ArrayList<>(resultado.stream()
                .filter(h -> !h.getFechaDeCarga().toLocalDate().isAfter(fechaReporteHasta))
                .toList());
            log.debug("[aplicarFiltros] Fecha reporte hasta {}: {} -> {} hechos",
                fechaReporteHasta, iniciales, resultado.size());
            iniciales = resultado.size();
        }

        // Filtro de fecha de acontecimiento desde
        if (fechaAcontecimientoDesde != null) {
            resultado = new ArrayList<>(resultado.stream()
                .filter(h -> !h.getFechaDeAcontecimiento().isBefore(fechaAcontecimientoDesde))
                .toList());
            log.debug("[aplicarFiltros] Fecha acontecimiento desde {}: {} -> {} hechos",
                fechaAcontecimientoDesde, iniciales, resultado.size());
            iniciales = resultado.size();
        }

        // Filtro de fecha de acontecimiento hasta
        if (fechaAcontecimientoHasta != null) {
            resultado = new ArrayList<>(resultado.stream()
                .filter(h -> !h.getFechaDeAcontecimiento().isAfter(fechaAcontecimientoHasta))
                .toList());
            log.debug("[aplicarFiltros] Fecha acontecimiento hasta {}: {} -> {} hechos",
                fechaAcontecimientoHasta, iniciales, resultado.size());
            iniciales = resultado.size();
        }

        // Filtro de fuente (por nombre de fuente)
        if (fuente != null && !fuente.trim().isEmpty()) {
            resultado = new ArrayList<>(resultado.stream()
                .filter(h -> h.getNombreFuente() != null && h.getNombreFuente().equals(fuente))
                .toList());
            log.debug("[aplicarFiltros] Fuente '{}': {} -> {} hechos", fuente, iniciales, resultado.size());
        }

        return resultado;
    }

    private int calcularCantidadFuentes() {
        return fuentesService.cantidadDeFuentes();
    }

    public PageResponse<ColeccionDTO> getPaginatedColecciones(int page, int size) {
        log.info("[ColeccionesService] Obteniendo colecciones paginadas - page: {}, size: {}", page, size);

        List<ColeccionDTO> resultado = new ArrayList<>();
        int start = page * size;
        int needed = size;

        // 1. Obtener colecciones locales
        ResponseEntity<List<ColeccionDTO>> localesResp = getAllColeccionesLocales();
        List<ColeccionDTO> coleccionesLocales = Optional.ofNullable(localesResp.getBody()).orElse(new ArrayList<>());
        log.debug("[ColeccionesService] Colecciones locales disponibles: {}", coleccionesLocales.size());

        // Aplicar paginación a locales
        if (start < coleccionesLocales.size()) {
            int endLocal = Math.min(start + size, coleccionesLocales.size());
            resultado.addAll(coleccionesLocales.subList(start, endLocal));
            needed = size - resultado.size();
            log.debug("[ColeccionesService] Agregadas {} colecciones locales, faltan: {}", resultado.size(), needed);
        } else {
            // Ajustar start para externas
            start = start - coleccionesLocales.size();
        }

        // 2. Obtener colecciones externas
        ResponseEntity<List<ColeccionDTO>> externasResp = getAllColeccionesExternas();
        List<ColeccionDTO> coleccionesExternas = Optional.ofNullable(externasResp.getBody()).orElse(new ArrayList<>());

        if (needed > 0 && !coleccionesExternas.isEmpty() && start < coleccionesExternas.size()) {
            log.debug("[ColeccionesService] Obteniendo colecciones externas para completar página");
            int endExterno = Math.min(start + needed, coleccionesExternas.size());
            resultado.addAll(coleccionesExternas.subList(start, endExterno));
            log.debug("[ColeccionesService] Agregadas {} colecciones externas", endExterno - start);
        }

        // Determinar si hay página siguiente
        int totalDisponibles = coleccionesLocales.size() + coleccionesExternas.size();
        boolean hasNext = (page + 1) * size < totalDisponibles;

        log.info("[ColeccionesService] Devolviendo {} colecciones para página {}, hasNext: {}", resultado.size(), page, hasNext);

        return new PageResponse<>(
                resultado,
                page,
                size,
                hasNext,
                page > 0,
                totalDisponibles
        );
    }
}

