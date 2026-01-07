package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.coleccion.ModoNavegacion;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.AAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.TipoAlgoritmoConsenso;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.impl.Absoluta;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.impl.MayoriaSimple;
import ar.utn.ba.ddsi.agregador.domain.coleccion.algoritmosConsenso.impl.MultiplesMenciones;
import ar.utn.ba.ddsi.agregador.domain.coleccion.Coleccion;
import ar.utn.ba.ddsi.agregador.domain.fuente.AFuente;
import ar.utn.ba.ddsi.agregador.domain.fuente.impl.FuenteTiempoReal;
import ar.utn.ba.ddsi.agregador.domain.coleccion.criterios.Criterio;
import ar.utn.ba.ddsi.agregador.dto.ColeccionDTO;
import ar.utn.ba.ddsi.agregador.dto.CriterioDTO;
import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.dto.PageResponse;
import ar.utn.ba.ddsi.agregador.dto.mapper.ColeccionMapper;
import ar.utn.ba.ddsi.agregador.dto.mapper.CriterioMapper;
import ar.utn.ba.ddsi.agregador.dto.mapper.HechoMapper;
import ar.utn.ba.ddsi.agregador.domain.hecho.EstadoHecho;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Transactional(readOnly = true)
    public ResponseEntity<ColeccionDTO> getOneColeccion() {
        List<Coleccion> coleccionesLocales = coleccionesRepository.findAll();

        if (!coleccionesLocales.isEmpty()) {
            Coleccion coleccion = coleccionesLocales.get(0);
            coleccion.getHechos().size();
            coleccion.getHechosConsensuados().size();
            ColeccionDTO coleccionDTO = coleccionMapper.toDTO(coleccion);
            return ResponseEntity.ok(coleccionDTO);
        }

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

    @Transactional(readOnly = true)
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
        
        if (page < 0) {
            log.warn("[ColeccionesService] Página inválida: {}, usando 0", page);
            page = 0;
        }
        if (size < 1) {
            log.warn("[ColeccionesService] Tamaño inválido: {}, usando 100", size);
            size = 100;
        }
        if (size > 200) {
            log.warn("[ColeccionesService] Tamaño muy grande: {}, limitando a 200", size);
            size = 200;
        }

        ModoNavegacion modo = null;
        if (modoStr != null && !modoStr.trim().isEmpty()) {
            try {
                modo = ModoNavegacion.valueOf(modoStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("[ColeccionesService] Modo inválido: {}, usando IRRESTRICTA por defecto", modoStr);
                modo = ModoNavegacion.IRRESTRICTA;
            }
        }

        try {
            UUID uuid = UUID.fromString(handle);
            Optional<Coleccion> coleccionOpt = coleccionesRepository.findById(uuid);

            if (coleccionOpt.isPresent()) {
                Coleccion coleccion = coleccionOpt.get();

                if (coleccion.getCriteriosDePertenencia() == null || coleccion.getCriteriosDePertenencia().isEmpty()) {
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

                ModoNavegacion modoNav = modo != null ? modo : ModoNavegacion.IRRESTRICTA;
                
                List<Long> fuentesTiempoReal = fuentesService.getFuentesTiempoReal();
                List<OrigenHecho> origenesTiempoReal = fuentesService.getOrigenesTiempoReal();
                boolean tieneHechosExternos = coleccion.tieneHechosExternos(
                    fuentesTiempoReal, origenesTiempoReal, fuentesService.getNombresFuentesTiempoReal());

                if (!tieneHechosExternos) {
                    return getHechosColeccionPaginatedFromDB(
                        coleccion, modoNav, categoria, fechaReporteDesde, fechaReporteHasta,
                        fechaAcontecimientoDesde, fechaAcontecimientoHasta, ubicacion, fuente, page, size);
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

        } catch (IllegalArgumentException e) {
            log.warn("[ColeccionesService] Handle inválido: {}", handle);
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
    }

    private ResponseEntity<PageResponse<HechoDTO>> getHechosColeccionPaginatedFromDB(
            Coleccion coleccion,
            ModoNavegacion modo,
            String categoria,
            LocalDate fechaReporteDesde,
            LocalDate fechaReporteHasta,
            LocalDateTime fechaAcontecimientoDesde,
            LocalDateTime fechaAcontecimientoHasta,
            String ubicacion,
            String fuente,
            int page,
            int size) {
        
        List<Hecho> hechosBase;
        if (modo == ModoNavegacion.CURADA) {
            hechosBase = new ArrayList<>(coleccion.getHechosConsensuados());
        } else {
            hechosBase = new ArrayList<>(coleccion.getHechos());
        }

        List<Hecho> hechosVisibles = hechosBase.stream()
            .filter(Hecho::esVisible)
            .toList();

        List<Hecho> hechosFiltrados = aplicarFiltros(
            hechosVisibles, categoria, fechaReporteDesde, fechaReporteHasta,
            fechaAcontecimientoDesde, fechaAcontecimientoHasta, ubicacion, fuente);

        long total = hechosFiltrados.size();
        int start = page * size;
        int end = Math.min(start + size, hechosFiltrados.size());

        if (start >= hechosFiltrados.size()) {
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

        List<Hecho> hechosPaginados = hechosFiltrados.subList(start, end);
        List<HechoDTO> hechosDTO = hechoMapper.toDTO(hechosPaginados);
        boolean hasNext = end < hechosFiltrados.size();

        PageResponse<HechoDTO> response = new PageResponse<>(
                hechosDTO,
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

    @Transactional(readOnly = true)
    public ResponseEntity<List<ColeccionDTO>> getAllColeccionesLocales() {

        List<Coleccion> coleccionesBD = coleccionesRepository.findAll();
        List<ColeccionDTO> dtosBD = coleccionesBD.stream()
            .map(coleccion -> {
                ColeccionDTO dto = coleccionMapper.toDTOWithoutHechos(coleccion);
                if (coleccion.getCriteriosDePertenencia() != null && !coleccion.getCriteriosDePertenencia().isEmpty()) {
                    List<Long> fuentesTiempoReal = fuentesService.getFuentesTiempoReal();
                    List<OrigenHecho> origenesTiempoReal = fuentesService.getOrigenesTiempoReal();
                    if (coleccion.tieneHechosExternos(fuentesTiempoReal, origenesTiempoReal, fuentesService.getNombresFuentesTiempoReal())) {
                        int cantidadTotal = calcularCantidadTotalHechos(coleccion);
                        dto.setCantidadHechos(cantidadTotal);
                    }
                }
                return dto;
            })
            .toList();

        return ResponseEntity.ok(dtosBD);
    }

    private int calcularCantidadTotalHechos(Coleccion coleccion) {
        try {
            ResponseEntity<List<HechoDTO>> respuesta = getHechosColeccionByHandle(
                coleccion.getHandle().toString(), null, null, null, null, null, null, null, ModoNavegacion.IRRESTRICTA);
            
            if (respuesta.getBody() != null) {
                return respuesta.getBody().size();
            }
        } catch (Exception e) {
        }
        
        return coleccion.getHechos() != null ? coleccion.getHechos().size() : 0;
    }

    public ResponseEntity<List<ColeccionDTO>> getAllColeccionesExternas() {

        List<ColeccionDTO> coleccionesExternas = new ArrayList<>();
        try {
            coleccionesExternas = Optional.ofNullable(
                fuentesService.getAllColecciones().getBody()
            ).orElse(List.of());
            
            coleccionesExternas = coleccionesExternas.stream()
                .map(coleccion -> {
                    if (coleccion.getCantidadHechos() == null) {
                        if (coleccion.getHechos() != null) {
                            coleccion.setCantidadHechos(coleccion.getHechos().size());
                        } else if (coleccion.getHechosConsensuados() != null) {
                            coleccion.setCantidadHechos(coleccion.getHechosConsensuados().size());
                        } else {
                            coleccion.setCantidadHechos(0);
                        }
                    }
                    return coleccion;
                })
                .toList();
            
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

        try {
            UUID uuid = UUID.fromString(handle);
            Optional<Coleccion> coleccionOpt = coleccionesRepository.findById(uuid);

            if (coleccionOpt.isPresent()) {
                Coleccion coleccion = coleccionOpt.get();

                if (coleccion.getCriteriosDePertenencia() == null || coleccion.getCriteriosDePertenencia().isEmpty()) {
                    return ResponseEntity.ok(new ArrayList<>());
                }

                ModoNavegacion modoNav = modo != null ? modo : ModoNavegacion.IRRESTRICTA;
                List<Hecho> hechos;

                if (modoNav == ModoNavegacion.CURADA) {
                    hechos = new ArrayList<>(coleccion.getHechosConsensuados().stream()
                        .filter(Hecho::esVisible)
                        .toList());
                } else {
                    hechos = new ArrayList<>(coleccion.getHechos().stream()
                        .filter(Hecho::esVisible)
                        .toList());
                }

                List<Long> fuentesTiempoReal = fuentesService.getFuentesTiempoReal();
                List<OrigenHecho> origenesTiempoReal = fuentesService.getOrigenesTiempoReal();

                if (coleccion.tieneHechosExternos(fuentesTiempoReal, origenesTiempoReal, fuentesService.getNombresFuentesTiempoReal())) {
                    try {
                        ResponseEntity<List<HechoDTO>> respuestaExternos = fuentesService.getAllHechosVisibles(
                            null, null, null, null, null, null);
                        List<HechoDTO> hechosExternosDTO = Optional.ofNullable(respuestaExternos.getBody()).orElse(List.of());
                        List<Hecho> hechosExternos = hechoMapper.fromDTO(hechosExternosDTO);

                        List<Hecho> hechosExternosFiltrados = coleccion.aplicarCriteriosDePertenencia(hechosExternos);

                        if (modoNav == ModoNavegacion.CURADA && coleccion.getAlgoritmoConsenso() != null) {
                            int cantidadFuentes = calcularCantidadFuentes();
                            List<Hecho> todosHechos = new ArrayList<>(hechos);
                            todosHechos.addAll(hechosExternosFiltrados);
                            List<Hecho> hechosPersistidos = hechos;
                            hechosExternosFiltrados = coleccion.aplicarAlgoritmoConsenso(todosHechos, cantidadFuentes)
                                .stream()
                                .filter(h -> !hechosPersistidos.contains(h))
                                .toList();
                        }

                        hechos.addAll(hechosExternosFiltrados);
                    } catch (Exception e) {
                        log.warn("[ColeccionesService] Error al obtener hechos externos: {}", e.getMessage());
                    }
                }

                hechos = aplicarFiltros(hechos, categoria,
                    fecha_reporte_desde, fecha_reporte_hasta,
                    fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion, fuente);

                List<HechoDTO> hechosDTO = hechoMapper.toDTO(hechos);
                return ResponseEntity.ok(hechosDTO);
            }

            try {
                List<ColeccionDTO> coleccionesExternas = Optional.ofNullable(
                    fuentesService.getAllColecciones().getBody()
                ).orElse(List.of());

                Optional<ColeccionDTO> coleccionExterna = coleccionesExternas.stream()
                    .filter(c -> uuid.equals(c.getHandle()))
                    .findFirst();

                if (coleccionExterna.isPresent()) {
                    ColeccionDTO coleccion = coleccionExterna.get();
                    ModoNavegacion modoNav = modo != null ? modo : ModoNavegacion.IRRESTRICTA;
                    List<HechoDTO> hechos;

                    if (modoNav == ModoNavegacion.CURADA && coleccion.getHechosConsensuados() != null) {
                        hechos = new ArrayList<>(coleccion.getHechosConsensuados());
                    } else if (coleccion.getHechos() != null) {
                        hechos = new ArrayList<>(coleccion.getHechos());
                    } else {
                        hechos = new ArrayList<>();
                    }

                    List<Hecho> hechosEntidad = hechoMapper.fromDTO(hechos);
                    hechosEntidad = aplicarFiltros(hechosEntidad, categoria,
                        fecha_reporte_desde, fecha_reporte_hasta,
                        fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion, fuente);

                    List<HechoDTO> hechosDTO = hechoMapper.toDTO(hechosEntidad);
                    return ResponseEntity.ok(hechosDTO);
                }
            } catch (Exception e) {
                log.warn("[ColeccionesService] Error consultando fuentes externas: {}", e.getMessage());
            }

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

            if (criterios.isEmpty()) {
                todosHechosFiltrados = new ArrayList<>();
                todosHechosConsensuados = new ArrayList<>();
            } else {
                // Optimización: usar query directa en lugar de findAll() + filter en memoria
                List<Hecho> hechosLocalesVisibles = hechosRepository.findByEstado(EstadoHecho.VISIBLE);

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
                    log.warn("[ColeccionesService] Error al obtener hechos externos: {}", e.getMessage());
                }
                }

                todosHechosFiltrados = coleccionTemporal.aplicarCriteriosDePertenencia(todosHechos);
                todosHechosConsensuados = coleccionTemporal.aplicarAlgoritmoConsenso(todosHechosFiltrados, calcularCantidadFuentes());
            }

            List<Hecho> hechosLocalesFiltrados = coleccionTemporal.separarHechosLocales(todosHechosFiltrados);
            List<Hecho> hechosLocalesConsensuados = coleccionTemporal.separarHechosLocales(todosHechosConsensuados);

            Coleccion coleccion = Coleccion.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .algoritmoConsenso(algoritmo)
                .criteriosDePertenencia(criterios)
                .hechos(hechosLocalesFiltrados)
                .hechosConsensuados(hechosLocalesConsensuados)
                .build();

            Coleccion coleccionGuardada = coleccionesRepository.saveAndFlush(coleccion);

            ColeccionDTO respuesta = coleccionMapper.toDTOWithoutHechos(coleccionGuardada);
            respuesta.setHechos(hechoMapper.toDTO(todosHechosFiltrados));
            respuesta.setHechosConsensuados(hechoMapper.toDTO(todosHechosConsensuados));


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
                cambiaronCriterios = true;

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

            coleccionActualizada.getHechos().size();
            coleccionActualizada.getHechosConsensuados().size();
            
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
        if (coleccion.getCriteriosDePertenencia() == null || coleccion.getCriteriosDePertenencia().isEmpty()) {
            coleccion.setHechos(new ArrayList<>());
            coleccion.setHechosConsensuados(new ArrayList<>());
            return;
        }

        // Optimización: usar query directa en lugar de findAll() + filter en memoria
        List<Hecho> hechosLocalesVisibles = hechosRepository.findByEstado(EstadoHecho.VISIBLE);

        List<Hecho> hechosFiltrados = coleccion.aplicarCriteriosDePertenencia(hechosLocalesVisibles);
        coleccion.setHechos(new ArrayList<>(hechosFiltrados));

        List<Hecho> hechosConsensuados = coleccion.aplicarAlgoritmoConsenso(hechosFiltrados, calcularCantidadFuentes());
        coleccion.setHechosConsensuados(new ArrayList<>(hechosConsensuados));
    }

    private void recalcularSoloConsenso(Coleccion coleccion) {
        List<Hecho> hechos = new ArrayList<>(coleccion.getHechos());
        List<Hecho> hechosConsensuados = coleccion.aplicarAlgoritmoConsenso(hechos, calcularCantidadFuentes());
        coleccion.setHechosConsensuados(new ArrayList<>(hechosConsensuados));

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

    @Transactional(readOnly = false)
    public void actualizarHechosSegunConsenso() {
        List<Coleccion> colecciones = coleccionesRepository.findAll();
        final int cantidadFuentes = calcularCantidadFuentes();

        // Optimización: usar query directa en lugar de findAll() + filter en memoria
        List<Hecho> hechosLocalesVisibles = hechosRepository.findByEstado(EstadoHecho.VISIBLE);

        for (Coleccion coleccion : colecciones) {
            coleccion.recalcularHechos(hechosLocalesVisibles, cantidadFuentes);
        }

        coleccionesRepository.saveAll(colecciones);
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

        if (categoria != null && !categoria.trim().isEmpty()) {
            resultado = new ArrayList<>(resultado.stream()
                .filter(h -> h.getCategoria() != null &&
                    h.getCategoria().getNombre() != null &&
                    h.getCategoria().getNombre().toLowerCase().contains(categoria.toLowerCase()))
                .toList());
            iniciales = resultado.size();
        }

        if (ubicacion != null && !ubicacion.trim().isEmpty()) {
            String provinciaNombre = ubicacion.trim();
            resultado = new ArrayList<>(resultado.stream()
                .filter(h -> h.getProvincia() != null &&
                    h.getProvincia().getNombre() != null &&
                    h.getProvincia().getNombre().equalsIgnoreCase(provinciaNombre))
                .toList());
            iniciales = resultado.size();
        }

        if (fechaReporteDesde != null) {
            resultado = new ArrayList<>(resultado.stream()
                .filter(h -> !h.getFechaDeCarga().toLocalDate().isBefore(fechaReporteDesde))
                .toList());
            iniciales = resultado.size();
        }

        if (fechaReporteHasta != null) {
            resultado = new ArrayList<>(resultado.stream()
                .filter(h -> !h.getFechaDeCarga().toLocalDate().isAfter(fechaReporteHasta))
                .toList());
            iniciales = resultado.size();
        }

        if (fechaAcontecimientoDesde != null) {
            resultado = new ArrayList<>(resultado.stream()
                .filter(h -> !h.getFechaDeAcontecimiento().isBefore(fechaAcontecimientoDesde))
                .toList());
            iniciales = resultado.size();
        }

        if (fechaAcontecimientoHasta != null) {
            resultado = new ArrayList<>(resultado.stream()
                .filter(h -> !h.getFechaDeAcontecimiento().isAfter(fechaAcontecimientoHasta))
                .toList());
        }

        if (fuente != null && !fuente.trim().isEmpty()) {
            resultado = new ArrayList<>(resultado.stream()
                .filter(h -> h.getNombreFuente() != null && h.getNombreFuente().equals(fuente))
                .toList());
        }

        return resultado;
    }

    private int calcularCantidadFuentes() {
        return fuentesService.cantidadDeFuentes();
    }

    @Transactional(readOnly = true)
    public PageResponse<ColeccionDTO> getPaginatedColecciones(int page, int size) {
        long startTime = System.currentTimeMillis();
        
        if (page < 0) {
            log.warn("[ColeccionesService] Página inválida: {}, usando 0", page);
            page = 0;
        }
        if (size < 1) {
            log.warn("[ColeccionesService] Tamaño inválido: {}, usando 100", size);
            size = 100;
        }
        if (size > 200) {
            log.warn("[ColeccionesService] Tamaño muy grande: {}, limitando a 200", size);
            size = 200;
        }
        log.info("[ColeccionesService] Obteniendo colecciones paginadas - page: {}, size: {}", page, size);

        long totalLocales = coleccionesRepository.count();
        
        List<ColeccionDTO> resultado = new ArrayList<>();
        int start = page * size;
        long totalExternas = 0;
        boolean hasNextExternas = false;
        int needed = size;

        boolean necesitaLocales = start < totalLocales;
        int offsetExterno = 0;

        if (necesitaLocales) {
            int endLocal = (int) Math.min(start + size, totalLocales);
            int pageLocal = start / size;
            int sizeLocal = endLocal - start;
            
            Pageable pageable = PageRequest.of(pageLocal, sizeLocal);
            Page<Coleccion> coleccionesPage = coleccionesRepository.findAll(pageable);
            
            List<ColeccionDTO> coleccionesLocales = coleccionesPage.getContent().stream()
                .map(coleccion -> {
                    ColeccionDTO dto = coleccionMapper.toDTOWithoutHechos(coleccion);
                    if (coleccion.getCriteriosDePertenencia() != null && !coleccion.getCriteriosDePertenencia().isEmpty()) {
                        List<Long> fuentesTiempoReal = fuentesService.getFuentesTiempoReal();
                        List<OrigenHecho> origenesTiempoReal = fuentesService.getOrigenesTiempoReal();
                        if (coleccion.tieneHechosExternos(fuentesTiempoReal, origenesTiempoReal, fuentesService.getNombresFuentesTiempoReal())) {
                            int cantidadTotal = calcularCantidadTotalHechos(coleccion);
                            dto.setCantidadHechos(cantidadTotal);
                        }
                    }
                    return dto;
                })
                .toList();
            
            int fromIndex = start % size;
            int toIndex = Math.min(fromIndex + needed, coleccionesLocales.size());
            if (fromIndex < coleccionesLocales.size()) {
                resultado.addAll(coleccionesLocales.subList(fromIndex, toIndex));
                needed = size - resultado.size();
            }
        } else {
            offsetExterno = (int) (start - totalLocales);
        }

        if (needed > 0) {
            try {
                PageResponse<ColeccionDTO> externasPaginated = getAllColeccionesExternasPaginated(offsetExterno, needed);
                
                if (externasPaginated != null) {
                    resultado.addAll(externasPaginated.getContent());
                    totalExternas = externasPaginated.getTotalElements();
                    hasNextExternas = externasPaginated.isHasNext();
                }
            } catch (Exception e) {
                log.warn("[ColeccionesService] Error al obtener colecciones externas: {}", e.getMessage());
            }
        }

        boolean hasNextLocales = necesitaLocales && (start + size < totalLocales);
        boolean hasNext = hasNextLocales || hasNextExternas || 
                         (!necesitaLocales && resultado.size() == size && hasNextExternas);
        long totalDisponibles = totalLocales + totalExternas;

        long totalTime = System.currentTimeMillis();
        log.info("[ColeccionesService] Devolviendo {} colecciones para página {} en {}ms, hasNext: {}", 
            resultado.size(), page, totalTime - startTime, hasNext);

        return new PageResponse<>(
                resultado,
                page,
                size,
                hasNext,
                page > 0,
                totalDisponibles
        );
    }
    
    private PageResponse<ColeccionDTO> getAllColeccionesExternasPaginated(int offset, int size) {
        try {
            List<PageResponse<ColeccionDTO>> respuestasPaginadas = new ArrayList<>();
            
            for (AFuente fuente : fuentesService.getFuentes()) {
                if (fuente instanceof FuenteTiempoReal) {
                    try {
                        int pageExterno = size > 0 ? offset / size : 0;
                        ResponseEntity<PageResponse<ColeccionDTO>> respuesta = fuente.getAllColeccionesPaginated(pageExterno, size);
                        
                        if (respuesta != null && respuesta.getBody() != null) {
                            respuestasPaginadas.add(respuesta.getBody());
                        }
                    } catch (Exception e) {
                    }
                }
            }
            
            if (!respuestasPaginadas.isEmpty()) {
                List<ColeccionDTO> todas = new ArrayList<>();
                long total = 0;
                boolean hasNext = false;
                
                for (PageResponse<ColeccionDTO> resp : respuestasPaginadas) {
                    List<ColeccionDTO> coleccionesConCantidad = resp.getContent().stream()
                        .map(coleccion -> {
                            if (coleccion.getCantidadHechos() == null) {
                                if (coleccion.getHechos() != null) {
                                    coleccion.setCantidadHechos(coleccion.getHechos().size());
                                } else if (coleccion.getHechosConsensuados() != null) {
                                    coleccion.setCantidadHechos(coleccion.getHechosConsensuados().size());
                                } else {
                                    coleccion.setCantidadHechos(0);
                                }
                            }
                            return coleccion;
                        })
                        .toList();
                    todas.addAll(coleccionesConCantidad);
                    total += resp.getTotalElements();
                    hasNext = hasNext || resp.isHasNext();
                }
                
                int pageExterno = size > 0 ? offset / size : 0;
                return new PageResponse<>(
                    todas, pageExterno, size,
                    hasNext || todas.size() >= size, offset > 0, total);
            }
            
            return null;
        } catch (Exception e) {
            log.warn("[ColeccionesService] Error al obtener colecciones externas paginadas: {}", e.getMessage());
            return null;
        }
    }
}

