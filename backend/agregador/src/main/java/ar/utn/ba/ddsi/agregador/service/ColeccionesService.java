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
        
        // Validar parámetros de paginación
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

                // Seleccionar hechos según modo: CURADA usa consensuados, IRRESTRICTA usa todos
                if (modoNav == ModoNavegacion.CURADA) {
                    hechos = new ArrayList<>(coleccion.getHechosConsensuados().stream()
                        .filter(Hecho::esVisible)
                        .toList());
                } else {
                    hechos = new ArrayList<>(coleccion.getHechos().stream()
                        .filter(Hecho::esVisible)
                        .toList());
                }

                // Verificar si la colección incluye hechos de fuentes externas (tiempo real)
                List<Long> fuentesTiempoReal = fuentesService.getFuentesTiempoReal();
                List<OrigenHecho> origenesTiempoReal = fuentesService.getOrigenesTiempoReal();

                if (coleccion.tieneHechosExternos(fuentesTiempoReal, origenesTiempoReal, fuentesService.getNombresFuentesTiempoReal())) {
                    try {
                        // Obtener hechos externos en tiempo real y aplicar criterios de pertenencia
                        ResponseEntity<List<HechoDTO>> respuestaExternos = fuentesService.getAllHechosVisibles(
                            null, null, null, null, null, null);
                        List<HechoDTO> hechosExternosDTO = Optional.ofNullable(respuestaExternos.getBody()).orElse(List.of());
                        List<Hecho> hechosExternos = hechoMapper.fromDTO(hechosExternosDTO);

                        List<Hecho> hechosExternosFiltrados = coleccion.aplicarCriteriosDePertenencia(hechosExternos);

                        // En modo CURADA, aplicar algoritmo de consenso también a los externos
                        // Combinar persistidos + externos, aplicar consenso, y filtrar solo los externos consensuados
                        if (modoNav == ModoNavegacion.CURADA && coleccion.getAlgoritmoConsenso() != null) {
                            int cantidadFuentes = calcularCantidadFuentes();
                            List<Hecho> todosHechos = new ArrayList<>(hechos);
                            todosHechos.addAll(hechosExternosFiltrados);
                            List<Hecho> hechosPersistidos = hechos; // Variable final para lambda
                            // Aplicar consenso a todos y quedarse solo con los externos que pasaron el consenso
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
                // Obtener hechos locales visibles desde BD
                List<Hecho> hechosLocalesVisibles = hechosRepository.findAll().stream()
                    .filter(Hecho::esVisible)
                    .toList();

                List<Hecho> todosHechos = new ArrayList<>(hechosLocalesVisibles);

                // Si la colección incluye hechos externos, obtenerlos en tiempo real y agregarlos
                if (coleccionTemporal.tieneHechosExternos(fuentesService.getFuentesTiempoReal(), fuentesService.getOrigenesTiempoReal(), fuentesService.getNombresFuentesTiempoReal())) {
                try {
                    ResponseEntity<List<HechoDTO>> respuestaExternos = fuentesService.getAllHechosVisibles(
                        null, null, null, null, null, null);
                    List<HechoDTO> hechosExternosDTO = Optional.ofNullable(respuestaExternos.getBody()).orElse(List.of());
                    List<Hecho> hechosExternos = hechoMapper.fromDTO(hechosExternosDTO);

                    if (!hechosExternos.isEmpty()) {
                        // Persistir categorías de hechos externos antes de agregarlos
                        categoriasService.persistirCategoriasDesdeHechos(hechosExternos);
                        todosHechos.addAll(hechosExternos);
                    }
                } catch (Exception e) {
                    log.warn("[ColeccionesService] Error al obtener hechos externos: {}", e.getMessage());
                }
                }

                // Aplicar criterios de pertenencia a todos los hechos (locales + externos)
                todosHechosFiltrados = coleccionTemporal.aplicarCriteriosDePertenencia(todosHechos);
                // Aplicar algoritmo de consenso sobre los hechos filtrados
                todosHechosConsensuados = coleccionTemporal.aplicarAlgoritmoConsenso(todosHechosFiltrados, calcularCantidadFuentes());
            }

            // Separar hechos locales (con ID) de externos (sin ID) para persistir solo los locales
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

            ColeccionDTO respuesta = coleccionMapper.toDTO(coleccionGuardada);
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

            // Actualizar criterios y detectar si cambiaron (requiere recálculo completo)
            if (body.has("criterios")) {
              JsonNode criteriosNode = body.get("criterios");

              if (criteriosNode.isNull()
                  || (criteriosNode.isArray() && criteriosNode.isEmpty())) {

                coleccion.setCriteriosDePertenencia(new ArrayList<>());
                cambiaronCriterios = true; // Cambio detectado: ahora no hay criterios

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
        if (coleccion.getCriteriosDePertenencia() == null || coleccion.getCriteriosDePertenencia().isEmpty()) {
            coleccion.setHechos(new ArrayList<>());
            coleccion.setHechosConsensuados(new ArrayList<>());
            return;
        }

        // Obtener todos los hechos locales visibles y aplicar criterios de pertenencia
        List<Hecho> hechosLocalesVisibles = hechosRepository.findAll().stream()
            .filter(Hecho::esVisible)
            .toList();

        // Aplicar criterios de pertenencia y consenso usando métodos de dominio
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

    @Transactional
    public void actualizarHechosSegunConsenso() {
        List<Coleccion> colecciones = coleccionesRepository.findAll();
        final int cantidadFuentes = calcularCantidadFuentes();

        List<Hecho> hechosLocalesVisibles = hechosRepository.findAll().stream()
            .filter(Hecho::esVisible)
            .toList();

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
            iniciales = resultado.size();
        }

        if (fechaReporteDesde != null) {
            resultado = new ArrayList<>(resultado.stream()
                .filter(h -> !h.getFechaDeCarga().toLocalDate().isBefore(fechaReporteDesde))
                .toList());
            log.debug("[aplicarFiltros] Fecha reporte desde {}: {} -> {} hechos",
                fechaReporteDesde, iniciales, resultado.size());
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
            log.debug("[aplicarFiltros] Fecha acontecimiento hasta {}: {} -> {} hechos",
                fechaAcontecimientoHasta, iniciales, resultado.size());
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

    public PageResponse<ColeccionDTO> getPaginatedColecciones(int page, int size) {
        // Validar parámetros de paginación
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

        List<ColeccionDTO> resultado = new ArrayList<>();
        int start = page * size;
        int needed = size;

        // Paginación híbrida: primero locales (BD), luego externas (fuentes)
        ResponseEntity<List<ColeccionDTO>> localesResp = getAllColeccionesLocales();
        List<ColeccionDTO> coleccionesLocales = Optional.ofNullable(localesResp.getBody()).orElse(new ArrayList<>());

        // Si la página está dentro de las locales, tomar de ahí
        if (start < coleccionesLocales.size()) {
            int endLocal = Math.min(start + size, coleccionesLocales.size());
            resultado.addAll(coleccionesLocales.subList(start, endLocal));
            needed = size - resultado.size();
        } else {
            // La página está más allá de las locales, ajustar índice para externas
            start = start - coleccionesLocales.size();
        }

        // Si aún faltan elementos, completar con colecciones externas
        ResponseEntity<List<ColeccionDTO>> externasResp = getAllColeccionesExternas();
        List<ColeccionDTO> coleccionesExternas = Optional.ofNullable(externasResp.getBody()).orElse(new ArrayList<>());

        if (needed > 0 && !coleccionesExternas.isEmpty() && start < coleccionesExternas.size()) {
            int endExterno = Math.min(start + needed, coleccionesExternas.size());
            resultado.addAll(coleccionesExternas.subList(start, endExterno));
        }

        // Calcular totales combinados para determinar si hay página siguiente
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

