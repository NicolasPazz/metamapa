package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.fuente.AFuente;
import ar.utn.ba.ddsi.agregador.domain.fuente.config.FuentesProperties;
import ar.utn.ba.ddsi.agregador.domain.fuente.config.FuenteConfig;
import ar.utn.ba.ddsi.agregador.domain.fuente.factory.TipoFuente;
import ar.utn.ba.ddsi.agregador.domain.hecho.Etiqueta;
import ar.utn.ba.ddsi.agregador.domain.ubicacion.Provincia;
import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
import ar.utn.ba.ddsi.agregador.dto.PageResponse;
import ar.utn.ba.ddsi.agregador.domain.hecho.Hecho;
import ar.utn.ba.ddsi.agregador.dto.mapper.HechoMapper;
import ar.utn.ba.ddsi.agregador.repositorie.IHechosRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HechosService {

    private final IHechosRepository hechosRepository;
    private final FuentesService fuentesService;
    private final HechoMapper hechoMapper;
    private final CategoriasService categoriasService;
    private final ProvinciaService provinciaService;
    private final FuentesProperties fuentesProperties;
    public ResponseEntity<List<HechoDTO>> getAll(
        String categoria,
        LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
        LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
        String ubicacion,
        String fuente) {

        log.info("[HechosService] Obteniendo todos los hechos con filtros: categoria={}, ubicacion={}, fuente={}",
            categoria, ubicacion, fuente);

        // Obtener hechos locales desde la BD (solo hechos VISIBLES)
        List<Hecho> hechosLocales = hechosRepository.findByFiltersConUbicacion(
            categoria,
            fecha_reporte_desde, fecha_reporte_hasta,
            fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
            ubicacion, fuente);

        List<HechoDTO> locales = hechoMapper.toDTO(hechosLocales);
        log.debug("[HechosService] Se obtuvieron {} hechos locales VISIBLES", locales.size());

        // Intentar obtener hechos de fuentes externas (resiliente a fallos)
        List<HechoDTO> externos = new ArrayList<>();
        try {
            ResponseEntity<List<HechoDTO>> respuestaExternos = fuentesService.getAllHechosVisibles(
                categoria,
                fecha_reporte_desde, fecha_reporte_hasta,
                fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
                ubicacion);

            externos = Optional.ofNullable(respuestaExternos.getBody()).orElse(List.of());
            
            // Filtrar por fuente si se especificó (por nombre de fuente)
            if (fuente != null && !fuente.trim().isEmpty()) {
                externos = externos.stream()
                    .filter(h -> h.getNombreFuente() != null && h.getNombreFuente().equals(fuente))
                    .toList();
            }
            
            log.debug("[HechosService] Se obtuvieron {} hechos externos", externos.size());
        } catch (Exception e) {
            log.warn("[HechosService] No se pudieron obtener hechos de fuentes externas: {}", e.getMessage());
            log.debug("[HechosService] Continuando solo con hechos locales");
        }

        // Combinar hechos locales y externos
        List<HechoDTO> combinados = new ArrayList<>(locales);
        combinados.addAll(externos);

        log.info("[HechosService] Total de hechos agregados: {} (locales: {}, externos: {})",
            combinados.size(), locales.size(), externos.size());
        return ResponseEntity.ok(combinados);
    }

    @Transactional
    public ResponseEntity<?> crearHecho(HechoDTO hechoInputDTO) {
        if (hechoInputDTO == null) {
            log.warn("[HechosService] Intento de crear hecho con DTO null");
            return ResponseEntity.badRequest().body("El DTO del hecho no puede ser null");
        }

        try {
            log.info("[HechosService] Creando nuevo hecho: {}", hechoInputDTO.getTitulo());

            // Convertir DTO a entidad
            Hecho hechoNuevo = hechoMapper.fromDTO(hechoInputDTO);

            // Asignar provincia basada en coordenadas o nombre
            if (hechoNuevo.getLatitud() != null && hechoNuevo.getLongitud() != null) {
                Provincia provincia =
                    provinciaService.obtenerProvinciaPorCoordenadas(
                        hechoNuevo.getLatitud(), hechoNuevo.getLongitud());
                hechoNuevo.setProvincia(provincia);
                log.debug("[HechosService] Provincia asignada: {}", provincia.getNombre());
            } else if (hechoInputDTO.getProvincia() != null) {
                Provincia provincia =
                    provinciaService.obtenerOCrearProvincia(hechoInputDTO.getProvincia());
                hechoNuevo.setProvincia(provincia);
            }

            // Asignar nombre de la fuente si no está asignado
            if (hechoNuevo.getNombreFuente() == null && hechoNuevo.getFuenteId() != null) {
                AFuente fuente = fuentesService.getFuenteById(hechoNuevo.getFuenteId());
                if (fuente != null) {
                    hechoNuevo.setNombreFuente(fuente.getNombre());
                }
            }

            // Normalizar categoría antes de persistir
            categoriasService.persistirCategoriasDesdeHechos(List.of(hechoNuevo));

            // Persistir el hecho
            Hecho hechoGuardado = hechosRepository.save(hechoNuevo);

            log.info("[HechosService] Hecho creado exitosamente con ID: {}", hechoGuardado.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(hechoMapper.toDTO(hechoGuardado));

        } catch (Exception e) {
            log.error("[HechosService] Error al crear hecho: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear el hecho: " + e.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<?> actualizarEtiquetas(Long idHecho, List<String> etiquetas) {
        if (idHecho == null) {
            log.warn("[HechosService] Intento de actualizar etiquetas sin ID de hecho");
            return ResponseEntity.badRequest().body("Debe proporcionar el ID del hecho");
        }

        if (etiquetas == null) {
            log.warn("[HechosService] Lista de etiquetas es null");
            return ResponseEntity.badRequest().body("Debe proporcionar una lista de etiquetas");
        }

        try {
            Hecho hecho = hechosRepository.findById(idHecho)
                .orElseThrow(() -> new IllegalArgumentException(
                    "No se encontró el Hecho con ID " + idHecho));

            hecho.getEtiquetas().clear();

            List<Etiqueta> nuevasEtiquetas = etiquetas.stream()
                .filter(nombre -> nombre != null && !nombre.trim().isEmpty())
                .map(nombre -> new Etiqueta(nombre.trim()))
                .toList();

            hecho.getEtiquetas().addAll(nuevasEtiquetas);

            Hecho hechoActualizado = hechosRepository.save(hecho);

            log.info("[HechosService] Etiquetas actualizadas para el hecho {}: {} etiquetas",
                idHecho, nuevasEtiquetas.size());

            return ResponseEntity.ok(hechoMapper.toDTO(hechoActualizado));

        } catch (IllegalArgumentException e) {
            log.warn("[HechosService] {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("[HechosService] Error al actualizar etiquetas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar las etiquetas: " + e.getMessage());
        }
    }

    public ResponseEntity<List<String>> getProvinciasConHechos() {
        log.info("[HechosService] Obteniendo provincias con hechos");
        return provinciaService.getProvinciasConHechos();
    }

    public ResponseEntity<List<String>> getFuentesConHechos() {
        log.info("[HechosService] Obteniendo fuentes con hechos");
        // Obtener nombres de fuentes desde la configuración
        List<String> nombresFuentesConfig = fuentesProperties.getConfig().stream()
            .map(FuenteConfig::getNombre)
            .toList();
        
        // Filtrar: incluir fuentes que tienen hechos visibles en la BD O que son de tiempo real
        // (las de tiempo real pueden tener hechos aunque no estén persistidos)
        List<String> fuentesConHechos = nombresFuentesConfig.stream()
            .filter(nombre -> {
                // Si tiene hechos en BD, incluirla
                if (hechosRepository.existsByNombreFuenteAndEstadoVisible(nombre)) {
                    return true;
                }
                // Si es de tiempo real, verificar si tiene hechos consultando la fuente
                FuenteConfig config = fuentesProperties.getConfig().stream()
                    .filter(c -> c.getNombre().equals(nombre))
                    .findFirst()
                    .orElse(null);
                if (config != null && config.getTipo() == TipoFuente.TIEMPO_REAL) {
                    try {
                        AFuente fuente = fuentesService.getFuenteById(config.getId());
                        if (fuente != null) {
                            ResponseEntity<List<HechoDTO>> respuesta = fuente.getAllHechosVisibles(null, null, null, null, null, null);
                            List<HechoDTO> hechos = Optional.ofNullable(respuesta.getBody()).orElse(List.of());
                            return !hechos.isEmpty();
                        }
                    } catch (Exception e) {
                        log.debug("[HechosService] Error al verificar hechos de fuente tiempo real {}: {}", nombre, e.getMessage());
                    }
                }
                return false;
            })
            .toList();
        
        log.debug("[HechosService] Se encontraron {} fuentes con hechos de {} configuradas", 
            fuentesConHechos.size(), nombresFuentesConfig.size());
        return ResponseEntity.ok(fuentesConHechos);
    }

    public ResponseEntity<HechoDTO> getOneHechoReciente() {
        log.info("[HechosService] Obteniendo un hecho reciente para landing");

        List<Hecho> hechos = hechosRepository.findByFiltersConUbicacion(
            null, null, null, null, null, null, null);

        if (hechos.isEmpty()) {
            log.warn("[HechosService] No hay hechos disponibles");
            return ResponseEntity.notFound().build();
        }

        // Tomar el primer hecho (más reciente)
        HechoDTO hechoDTO = hechoMapper.toDTO(hechos.get(0));
        log.info("[HechosService] Hecho reciente obtenido: {}", hechoDTO.getTitulo());

        return ResponseEntity.ok(hechoDTO);
    }

    public ResponseEntity<List<HechoDTO>> getAllPaginated(
        String categoria,
        LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
        LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
        String ubicacion,
        String fuente,
        int page,
        int size) {

        log.info("[HechosService] Obteniendo hechos paginados: page={}, size={}", page, size);

        // Obtener todos los hechos con filtros
        List<Hecho> hechosLocales = hechosRepository.findByFiltersConUbicacion(
            categoria,
            fecha_reporte_desde, fecha_reporte_hasta,
            fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
            ubicacion, fuente);

        List<HechoDTO> locales = hechoMapper.toDTO(hechosLocales);

        // Intentar obtener hechos externos
        List<HechoDTO> externos = new ArrayList<>();
        try {
            ResponseEntity<List<HechoDTO>> respuestaExternos = fuentesService.getAllHechosVisibles(
                categoria,
                fecha_reporte_desde, fecha_reporte_hasta,
                fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
                ubicacion);
            externos = Optional.ofNullable(respuestaExternos.getBody()).orElse(List.of());
            
            // Filtrar por fuente si se especificó (por nombre de fuente)
            if (fuente != null && !fuente.trim().isEmpty()) {
                externos = externos.stream()
                    .filter(h -> h.getNombreFuente() != null && h.getNombreFuente().equals(fuente))
                    .toList();
            }
        } catch (Exception e) {
            log.warn("[HechosService] No se pudieron obtener hechos externos: {}", e.getMessage());
        }

        // Combinar
        List<HechoDTO> combinados = new ArrayList<>(locales);
        combinados.addAll(externos);

        // Paginar
        int start = page * size;
        int end = Math.min(start + size, combinados.size());

        if (start >= combinados.size()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<HechoDTO> paginados = combinados.subList(start, end);
        log.info("[HechosService] Devolviendo {} hechos (página {} de tamaño {})",
                 paginados.size(), page, size);

        return ResponseEntity.ok(paginados);
    }

    public ResponseEntity<List<HechoDTO>> getAllLocal(
        String categoria,
        LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
        LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
        String ubicacion,
        String fuente) {

        log.info("[HechosService] Obteniendo hechos locales con filtros");

        List<Hecho> hechosLocales = hechosRepository.findByFiltersConUbicacion(
            categoria,
            fecha_reporte_desde, fecha_reporte_hasta,
            fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
            ubicacion, fuente);

        List<HechoDTO> locales = hechoMapper.toDTO(hechosLocales);
        log.info("[HechosService] Hechos locales obtenidos: {}", locales.size());

        return ResponseEntity.ok(locales);
    }

    public ResponseEntity<List<HechoDTO>> getAllExternal(
        String categoria,
        LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
        LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
        String ubicacion,
        String fuente) {

        log.info("[HechosService] Obteniendo hechos externos con filtros");

        List<HechoDTO> externos = new ArrayList<>();
        try {
            ResponseEntity<List<HechoDTO>> respuestaExternos = fuentesService.getAllHechosVisibles(
                categoria,
                fecha_reporte_desde, fecha_reporte_hasta,
                fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
                ubicacion);

            externos = Optional.ofNullable(respuestaExternos.getBody()).orElse(List.of());
            
            // Filtrar por fuente si se especificó (por nombre de fuente)
            if (fuente != null && !fuente.trim().isEmpty()) {
                externos = externos.stream()
                    .filter(h -> h.getNombreFuente() != null && h.getNombreFuente().equals(fuente))
                    .toList();
            }
            
            log.info("[HechosService] Hechos externos obtenidos: {}", externos.size());
        } catch (Exception e) {
            log.warn("[HechosService] No se pudieron obtener hechos externos: {}", e.getMessage());
        }

        return ResponseEntity.ok(externos);
    }

    public PageResponse<HechoDTO> getPaginatedHechos(
            String categoria,
            LocalDate fecha_reporte_desde,
            LocalDate fecha_reporte_hasta,
            LocalDateTime fecha_acontecimiento_desde,
            LocalDateTime fecha_acontecimiento_hasta,
            String ubicacion,
            String fuente,
            int page,
            int size) {

        log.info("[HechosService] Obteniendo hechos paginados - page: {}, size: {}", page, size);

        List<HechoDTO> resultado = new ArrayList<>();
        int start = page * size;
        int needed = size;
        long totalLocales = 0;
        long totalExternos = 0;

        // 1. Obtener hechos locales (siempre necesarios para calcular totales y para páginas tempranas)
        ResponseEntity<List<HechoDTO>> localesResp = getAllLocal(
                categoria, fecha_reporte_desde, fecha_reporte_hasta,
                fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion, fuente);

        List<HechoDTO> hechosLocales = Optional.ofNullable(localesResp.getBody()).orElse(new ArrayList<>());
        totalLocales = hechosLocales.size();
        log.debug("[HechosService] Hechos locales disponibles: {}", totalLocales);

        // Aplicar paginación a locales
        if (start < hechosLocales.size()) {
            int endLocal = Math.min(start + size, hechosLocales.size());
            resultado.addAll(hechosLocales.subList(start, endLocal));
            needed = size - resultado.size();
            log.debug("[HechosService] Agregados {} hechos locales, faltan: {}", resultado.size(), needed);
        } else {
            // Ajustar start para externos
            start = start - (int)totalLocales;
        }

        // 2. Solo obtener externos si realmente se necesitan para esta página
        List<HechoDTO> hechosExternos = new ArrayList<>();
        if (needed > 0 || start >= 0) {
            try {
                ResponseEntity<List<HechoDTO>> externosResp = getAllExternal(
                        categoria, fecha_reporte_desde, fecha_reporte_hasta,
                        fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion, fuente);
                hechosExternos = Optional.ofNullable(externosResp.getBody()).orElse(new ArrayList<>());
                totalExternos = hechosExternos.size();
                
                if (needed > 0 && !hechosExternos.isEmpty() && start >= 0 && start < hechosExternos.size()) {
                    log.debug("[HechosService] Obteniendo hechos externos para completar página");
                    int endExterno = Math.min(start + needed, hechosExternos.size());
                    resultado.addAll(hechosExternos.subList(start, endExterno));
                    log.debug("[HechosService] Agregados {} hechos externos", endExterno - start);
                }
            } catch (Exception e) {
                log.warn("[HechosService] No se pudieron obtener hechos externos: {}", e.getMessage());
            }
        }

        // Determinar si hay página siguiente
        long totalDisponibles = totalLocales + totalExternos;
        boolean hasNext = (page + 1) * size < totalDisponibles;

        log.info("[HechosService] Devolviendo {} hechos para página {}, hasNext: {} (total: {})", 
                resultado.size(), page, hasNext, totalDisponibles);

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