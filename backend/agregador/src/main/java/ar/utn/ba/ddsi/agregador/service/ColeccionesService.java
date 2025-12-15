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
import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.dto.mapper.ColeccionMapper;
import ar.utn.ba.ddsi.agregador.dto.mapper.CriterioMapper;
import ar.utn.ba.ddsi.agregador.dto.mapper.HechoMapper;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.repositorie.IColeccionesRepository;
import ar.utn.ba.ddsi.agregador.repositorie.IHechosRepository;
import java.time.LocalDate;
import java.util.ArrayList;
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

     public ResponseEntity<List<ColeccionDTO>> getAll() {
        log.info("[ColeccionesService] Obteniendo todas las colecciones");
        List<Coleccion> colecciones = coleccionesRepository.findAll();
        List<ColeccionDTO> dtos = colecciones.stream()
            .map(coleccionMapper::toDTO)
            .toList();
        return ResponseEntity.ok(dtos);
    }

    public ResponseEntity<List<HechoDTO>> getHechosColeccionByHandle(
        String handle,
        String categoria,
        LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
        LocalDate fecha_acontecimiento_desde, LocalDate fecha_acontecimiento_hasta,
        String ubicacion,
        ModoNavegacion modo) {

        log.info("[ColeccionesService] Obteniendo hechos de colección: handle={}, modo={}", handle, modo);

        try {
            UUID uuid = UUID.fromString(handle);
            Optional<Coleccion> coleccionOpt = coleccionesRepository.findById(uuid);

            if (coleccionOpt.isEmpty()) {
                log.warn("[ColeccionesService] Colección no encontrada: {}", handle);
                return ResponseEntity.notFound().build();
            }

            Coleccion coleccion = coleccionOpt.get();
            Criterio criterio = coleccion.getCriterioDePertenencia();

            // Obtener todos los hechos disponibles (locales + externos)
            List<Hecho> hechosLocales = hechosRepository.findAll().stream()
                .filter(Hecho::esVisible)
                .toList();

            ResponseEntity<List<HechoDTO>> respuestaExternos = fuentesService.getAllHechosVisibles(
                null, null, null, null, null, null);
            List<HechoDTO> hechosExternosDTO = Optional.ofNullable(respuestaExternos.getBody()).orElse(List.of());
            List<Hecho> hechosExternos = hechoMapper.fromDTO(hechosExternosDTO);

            List<Hecho> todosLosHechos = new ArrayList<>(hechosLocales);
            todosLosHechos.addAll(hechosExternos);

            // Aplicar criterio de pertenencia de la colección
            List<Hecho> hechosFiltrados = todosLosHechos;
            if (criterio != null) {
                hechosFiltrados = todosLosHechos.stream()
                    .filter(criterio::cumple)
                    .toList();
                log.debug("[ColeccionesService] Filtrados por criterio de pertenencia: {} hechos", hechosFiltrados.size());
            }

            // Aplicar modo de navegación (curada/irrestricta)
            ModoNavegacion modoNav = modo != null ? modo : ModoNavegacion.IRRESTRICTA;
            if (modoNav == ModoNavegacion.CURADA && coleccion.getAlgoritmoConsenso() != null) {
                // Calcular consenso
                int cantidadFuentes = calcularCantidadFuentes();
                hechosFiltrados = hechosFiltrados.stream()
                    .filter(h -> coleccion.getAlgoritmoConsenso().esConsensuado(h, cantidadFuentes, todosLosHechos))
                    .toList();
                log.debug("[ColeccionesService] Filtrados por consenso: {} hechos", hechosFiltrados.size());
            }

            // Aplicar filtros adicionales
            hechosFiltrados = aplicarFiltros(hechosFiltrados, categoria,
                fecha_reporte_desde, fecha_reporte_hasta,
                fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion);

            List<HechoDTO> hechosDTO = hechoMapper.toDTO(hechosFiltrados);
            log.info("[ColeccionesService] Retornando {} hechos de la colección {}", hechosDTO.size(), handle);
            return ResponseEntity.ok(hechosDTO);

        } catch (IllegalArgumentException e) {
            log.warn("[ColeccionesService] Handle inválido: {}", handle);
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

            // Crear algoritmo de consenso si se especificó
            AAlgoritmoConsenso algoritmo = null;
            if (dto.getAlgoritmoConsenso() != null) {
                algoritmo = crearAlgoritmoConsenso(dto.getAlgoritmoConsenso());
            }

            // Obtener hechos iniciales
            List<Hecho> hechosLocales = hechosRepository.findAll().stream()
                .filter(Hecho::esVisible)
                .toList();

            ResponseEntity<List<HechoDTO>> respuestaExternos = fuentesService.getAllHechosVisibles(
                null, null, null, null, null, null);
            List<HechoDTO> hechosExternosDTO = Optional.ofNullable(respuestaExternos.getBody()).orElse(List.of());
            List<Hecho> hechosExternos = hechoMapper.fromDTO(hechosExternosDTO);

            List<Hecho> hechosDisponibles = new ArrayList<>(hechosLocales);
            hechosDisponibles.addAll(hechosExternos);

            // Normalizar categorías de hechos externos
            if (!hechosExternos.isEmpty()) {
                categoriasService.persistirCategoriasDesdeHechos(hechosExternos);
            }

            // Crear la colección
            Coleccion coleccion = Coleccion.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .algoritmoConsenso(algoritmo)
                .hechos(hechosDisponibles)
                .build();

            // Aplicar criterio si se especificó
            if (dto.getCriterioDePertenencia() != null) {
                Criterio criterio = criterioMapper.fromDTO(dto.getCriterioDePertenencia());
                coleccion.setCriterioDePertenencia(criterio);
                log.debug("[ColeccionesService] Criterio de pertenencia aplicado a la colección");
            }

            // Actualizar hechos según criterio y consenso
            int cantidadFuentes = calcularCantidadFuentes();
            coleccion.actualizarHechos();
            coleccion.actualizarHechosConsensuados(cantidadFuentes);

            Coleccion coleccionGuardada = coleccionesRepository.save(coleccion);
            log.info("[ColeccionesService] Colección creada con handle: {}", coleccionGuardada.getHandle());

            return ResponseEntity.status(HttpStatus.CREATED).body(coleccionMapper.toDTO(coleccionGuardada));

        } catch (Exception e) {
            log.error("[ColeccionesService] Error al crear colección: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear la colección: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> actualizarColeccion(String handle, ColeccionDTO dto) {
        try {
            UUID uuid = UUID.fromString(handle);
            Optional<Coleccion> coleccionOpt = coleccionesRepository.findById(uuid);

            if (coleccionOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Coleccion coleccion = coleccionOpt.get();

            // Actualizar título y descripción si se proporcionan
            if (dto.getTitulo() != null && !dto.getTitulo().isBlank()) {
                coleccion.setTitulo(dto.getTitulo());
            }
            if (dto.getDescripcion() != null && !dto.getDescripcion().isBlank()) {
                coleccion.setDescripcion(dto.getDescripcion());
            }

            // Actualizar algoritmo de consenso si se proporciona
            if (dto.getAlgoritmoConsenso() != null) {
                AAlgoritmoConsenso algoritmo = crearAlgoritmoConsenso(dto.getAlgoritmoConsenso());
                coleccion.setAlgoritmoConsenso(algoritmo);
            }

            // Recalcular consenso
            int cantidadFuentes = calcularCantidadFuentes();
            coleccion.actualizarHechosConsensuados(cantidadFuentes);

            Coleccion coleccionActualizada = coleccionesRepository.save(coleccion);
            return ResponseEntity.ok(coleccionMapper.toDTO(coleccionActualizada));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Handle inválido");
        } catch (Exception e) {
            log.error("[ColeccionesService] Error al actualizar colección: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar la colección");
        }
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
        int cantidadFuentes = calcularCantidadFuentes();

        for (Coleccion coleccion : colecciones) {
            coleccion.actualizarHechosConsensuados(cantidadFuentes);
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
                                       LocalDate fechaAcontecimientoDesde, LocalDate fechaAcontecimientoHasta,
                                       String ubicacion) {
        return hechos.stream()
            .filter(h -> categoria == null || (h.getCategoria() != null &&
                h.getCategoria().getNombre() != null &&
                h.getCategoria().getNombre().toLowerCase().contains(categoria.toLowerCase())))
            .filter(h -> ubicacion == null ||
                (h.getLatitud() + "," + h.getLongitud()).equals(ubicacion))
            .filter(h -> fechaReporteDesde == null ||
                !h.getFechaDeCarga().toLocalDate().isBefore(fechaReporteDesde))
            .filter(h -> fechaReporteHasta == null ||
                !h.getFechaDeCarga().toLocalDate().isAfter(fechaReporteHasta))
            .filter(h -> fechaAcontecimientoDesde == null ||
                !h.getFechaDeAcontecimiento().isBefore(fechaAcontecimientoDesde))
            .filter(h -> fechaAcontecimientoHasta == null ||
                !h.getFechaDeAcontecimiento().isAfter(fechaAcontecimientoHasta))
            .toList();
    }

    private int calcularCantidadFuentes() {
        return fuentesService.cantidadDeFuentes();
    }
}

