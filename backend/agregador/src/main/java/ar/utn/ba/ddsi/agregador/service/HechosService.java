package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.fuente.AFuente;
import ar.utn.ba.ddsi.agregador.domain.fuente.config.FuentesProperties;
import ar.utn.ba.ddsi.agregador.domain.fuente.config.FuenteConfig;
import ar.utn.ba.ddsi.agregador.domain.fuente.factory.TipoFuente;
import ar.utn.ba.ddsi.agregador.domain.fuente.impl.FuenteTiempoReal;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
public class HechosService {

    private final IHechosRepository hechosRepository;
    private final FuentesService fuentesService;
    private final HechoMapper hechoMapper;
    private final CategoriasService categoriasService;
    private final ProvinciaService provinciaService;
    private final FuentesProperties fuentesProperties;
    
    // Executor para llamadas asíncronas a fuentes externas
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    @Transactional(readOnly = true)
    public ResponseEntity<List<HechoDTO>> getAll(
        String categoria,
        LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
        LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
        String ubicacion,
        String fuente) {
        
        // #region agent log
        long startTime = System.currentTimeMillis();
        log.info("[HechosService] getAll - INICIO - categoria: {}, ubicacion: {}, fuente: {}", categoria, ubicacion, fuente);
        // #endregion

        List<Hecho> hechosLocales = hechosRepository.findByFiltersConUbicacion(
            categoria,
            fecha_reporte_desde, fecha_reporte_hasta,
            fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
            ubicacion, fuente);
        
        // #region agent log
        long queryTime = System.currentTimeMillis();
        log.info("[HechosService] getAll - QUERY_COMPLETA - tiempo: {}ms, cantidad: {}", queryTime - startTime, hechosLocales.size());
        // #endregion

        List<HechoDTO> locales = hechoMapper.toDTO(hechosLocales);
        
        // #region agent log
        long mappingTime = System.currentTimeMillis();
        log.info("[HechosService] getAll - MAPEO_COMPLETO - tiempo: {}ms, total: {}ms", mappingTime - queryTime, mappingTime - startTime);
        // #endregion

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
            log.warn("[HechosService] Error al obtener hechos de fuentes externas en getAll: {}", e.getMessage());
        }

        List<HechoDTO> combinados = new ArrayList<>(locales);
        combinados.addAll(externos);
        return ResponseEntity.ok(combinados);
    }

    @Transactional
    @CacheEvict(value = "hechosPrimeraPagina", allEntries = true)
    public ResponseEntity<?> crearHecho(HechoDTO hechoInputDTO) {
        if (hechoInputDTO == null) {
            log.warn("[HechosService] Intento de crear hecho con DTO null");
            return ResponseEntity.badRequest().body("El DTO del hecho no puede ser null");
        }

        try {

            Hecho hechoNuevo = hechoMapper.fromDTO(hechoInputDTO);

            if (hechoNuevo.getLatitud() != null && hechoNuevo.getLongitud() != null) {
                Provincia provincia =
                    provinciaService.obtenerProvinciaPorCoordenadas(
                        hechoNuevo.getLatitud(), hechoNuevo.getLongitud());
                hechoNuevo.setProvincia(provincia);
            } else if (hechoInputDTO.getProvincia() != null) {
                Provincia provincia =
                    provinciaService.obtenerOCrearProvincia(hechoInputDTO.getProvincia());
                hechoNuevo.setProvincia(provincia);
            }

            if (hechoNuevo.getNombreFuente() == null && hechoNuevo.getFuenteId() != null) {
                AFuente fuente = fuentesService.getFuenteById(hechoNuevo.getFuenteId());
                if (fuente != null) {
                    hechoNuevo.asignarNombreFuenteSiNoTiene(fuente.getNombre());
                }
            }

            categoriasService.persistirCategoriasDesdeHechos(List.of(hechoNuevo));
            Hecho hechoGuardado = hechosRepository.save(hechoNuevo);

            return ResponseEntity.status(HttpStatus.CREATED).body(hechoMapper.toDTO(hechoGuardado));

        } catch (Exception e) {
            log.error("[HechosService] Error al crear hecho: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear el hecho: " + e.getMessage());
        }
    }

    @Transactional
    @CacheEvict(value = "hechosPrimeraPagina", allEntries = true)
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

    @Transactional
    @CacheEvict(value = "hechosPrimeraPagina", allEntries = true)
    public ResponseEntity<?> ocultarHecho(Long idHecho) {
        log.info("[HechosService] Ocultando hecho con ID: {}", idHecho);
        
        try {
            Hecho hecho = hechosRepository.findById(idHecho)
                .orElseThrow(() -> new IllegalArgumentException(
                    "No se encontró el Hecho con ID " + idHecho));
            
            hecho.ocultar();
            hechosRepository.save(hecho);
            
            log.info("[HechosService] Hecho {} ocultado exitosamente", idHecho);
            return ResponseEntity.ok("Hecho ocultado exitosamente");
            
        } catch (IllegalArgumentException e) {
            log.warn("[HechosService] {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("[HechosService] Error al ocultar hecho: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al ocultar el hecho: " + e.getMessage());
        }
    }

    @Cacheable(value = "provincias", unless = "#result == null || #result.getBody().isEmpty()")
    public ResponseEntity<List<String>> getProvinciasConHechos() {
        return provinciaService.getProvinciasConHechos();
    }

    public ResponseEntity<List<String>> getFuentesConHechos() {
        List<String> nombresFuentesConfig = fuentesProperties.getConfig().stream()
            .map(FuenteConfig::getNombre)
            .toList();
        
        List<String> fuentesConHechos = nombresFuentesConfig.stream()
            .filter(nombre -> {
                if (hechosRepository.existsByNombreFuenteAndEstadoVisible(nombre)) {
                    return true;
                }
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
        
        return ResponseEntity.ok(fuentesConHechos);
    }

    public ResponseEntity<HechoDTO> getOneHechoReciente() {

        List<Hecho> hechos = hechosRepository.findByFiltersConUbicacion(
            null, null, null, null, null, null, null);

        if (hechos.isEmpty()) {
            log.warn("[HechosService] No hay hechos disponibles");
            return ResponseEntity.notFound().build();
        }

        HechoDTO hechoDTO = hechoMapper.toDTO(hechos.get(0));
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

        List<Hecho> hechosLocales = hechosRepository.findByFiltersConUbicacion(
            categoria,
            fecha_reporte_desde, fecha_reporte_hasta,
            fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
            ubicacion, fuente);

        List<HechoDTO> locales = hechoMapper.toDTO(hechosLocales);

        List<HechoDTO> externos = new ArrayList<>();
        try {
            ResponseEntity<List<HechoDTO>> respuestaExternos = fuentesService.getAllHechosVisibles(
                categoria,
                fecha_reporte_desde, fecha_reporte_hasta,
                fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
                ubicacion);
            externos = Optional.ofNullable(respuestaExternos.getBody()).orElse(List.of());
            
            if (fuente != null && !fuente.trim().isEmpty()) {
                externos = externos.stream()
                    .filter(h -> h.getNombreFuente() != null && h.getNombreFuente().equals(fuente))
                    .toList();
            }
        } catch (Exception e) {
            log.warn("[HechosService] Error al obtener hechos externos en getAllPaginated: {}", e.getMessage());
        }

        List<HechoDTO> combinados = new ArrayList<>(locales);
        combinados.addAll(externos);

        int start = page * size;
        int end = Math.min(start + size, combinados.size());

        if (start >= combinados.size()) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<HechoDTO> paginados = combinados.subList(start, end);

        return ResponseEntity.ok(paginados);
    }

    public ResponseEntity<List<HechoDTO>> getAllLocal(
        String categoria,
        LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
        LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
        String ubicacion,
        String fuente) {


        List<Hecho> hechosLocales = hechosRepository.findByFiltersConUbicacion(
            categoria,
            fecha_reporte_desde, fecha_reporte_hasta,
            fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
            ubicacion, fuente);

        List<HechoDTO> locales = hechoMapper.toDTO(hechosLocales);

        return ResponseEntity.ok(locales);
    }

    public ResponseEntity<List<HechoDTO>> getAllExternal(
        String categoria,
        LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
        LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
        String ubicacion,
        String fuente) {


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
            log.warn("[HechosService] Error al obtener hechos externos en getAllExternal: {}", e.getMessage());
        }

        return ResponseEntity.ok(externos);
    }

    @Cacheable(value = "hechosPrimeraPagina", 
               condition = "#page == 0 && #categoria == null && #fecha_reporte_desde == null && #fecha_reporte_hasta == null && #fecha_acontecimiento_desde == null && #fecha_acontecimiento_hasta == null && #ubicacion == null && #fuente == null",
               unless = "#result == null || #result.getContent().isEmpty()")
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
        
        long startTime = System.currentTimeMillis();
        
        if (page < 0) {
            log.warn("[HechosService] Página inválida: {}, usando 0", page);
            page = 0;
        }
        if (size < 1) {
            log.warn("[HechosService] Tamaño inválido: {}, usando 100", size);
            size = 100;
        }
        if (size > 200) {
            log.warn("[HechosService] Tamaño muy grande: {}, limitando a 200", size);
            size = 200;
        }

        // Calcular total de hechos locales (sin cargar todos los datos)
        long totalLocales = hechosRepository.countByFiltersConUbicacion(
                categoria, fecha_reporte_desde, fecha_reporte_hasta,
                fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion, fuente);
        
        long queryTime = System.currentTimeMillis();

        List<HechoDTO> resultado = new ArrayList<>();
        int start = page * size;
        long totalExternos = 0;
        boolean hasNextExternos = false;

        boolean necesitaLocales = start < totalLocales;
        int offsetExterno = 0;
        int needed = size;

        if (necesitaLocales) {
            int endLocal = (int) Math.min(start + size, totalLocales);
            int pageLocal = start / size;
            int sizeLocal = endLocal - start;
            
            Pageable pageable = PageRequest.of(pageLocal, sizeLocal);
            Page<Hecho> hechosPage = hechosRepository.findByFiltersConUbicacionPaginated(
                    categoria, fecha_reporte_desde, fecha_reporte_hasta,
                    fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion, fuente, pageable);
            
            List<HechoDTO> hechosLocales = hechoMapper.toDTO(hechosPage.getContent());
            
            int fromIndex = start % size;
            int toIndex = Math.min(fromIndex + needed, hechosLocales.size());
            if (fromIndex < hechosLocales.size()) {
                resultado.addAll(hechosLocales.subList(fromIndex, toIndex));
                needed = size - resultado.size();
            }
        } else {
            offsetExterno = (int) (start - totalLocales);
        }

        if (needed > 0) {
            try {
                PageResponse<HechoDTO> externosPaginated = getAllExternalPaginated(
                        categoria, fecha_reporte_desde, fecha_reporte_hasta,
                        fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion, fuente,
                        offsetExterno, needed);
                
                if (externosPaginated != null) {
                    resultado.addAll(externosPaginated.getContent());
                    totalExternos = externosPaginated.getTotalElements();
                    hasNextExternos = externosPaginated.isHasNext();
                }
            } catch (Exception e) {
                log.warn("[HechosService] Error al obtener hechos externos: {}", e.getMessage());
            }
        }

        // Calcular hasNext: si hay más locales después de esta página O si hay más externos
        boolean hasNextLocales = necesitaLocales && (start + size < totalLocales);
        boolean hasNext = hasNextLocales || hasNextExternos || 
                         (!necesitaLocales && resultado.size() == size && hasNextExternos);
        long totalDisponibles = totalLocales + totalExternos;

        long totalTime = System.currentTimeMillis();
        log.info("[HechosService] Tiempo total getPaginatedHechos: {}ms (locales: {}, externos: {})", 
                totalTime - startTime, totalLocales, totalExternos);

        return new PageResponse<>(
                resultado,
                page,
                size,
                hasNext,
                page > 0,
                totalDisponibles
        );
    }
    
    private PageResponse<HechoDTO> getAllExternalPaginated(
            String categoria,
            LocalDate fecha_reporte_desde,
            LocalDate fecha_reporte_hasta,
            LocalDateTime fecha_acontecimiento_desde,
            LocalDateTime fecha_acontecimiento_hasta,
            String ubicacion,
            String fuente,
            int offset,
            int size) {
        
        try {
            List<PageResponse<HechoDTO>> respuestasPaginadas = new ArrayList<>();
            List<AFuente> fuentes = fuentesService.getFuentes();
            
            for (AFuente fuenteObj : fuentes) {
                // Si hay un filtro de fuente, solo procesar la fuente que coincide
                if (fuente != null && !fuente.trim().isEmpty()) {
                    String fuenteFiltroNormalizada = fuente.trim();
                    String nombreFuenteObj = fuenteObj.getNombre() != null ? fuenteObj.getNombre().trim() : null;
                    if (!fuenteFiltroNormalizada.equals(nombreFuenteObj)) {
                        continue; // Saltar fuentes que no coinciden con el filtro
                    }
                }
                if (fuenteObj instanceof FuenteTiempoReal) {
                    try {
                        int pageExterno = size > 0 ? offset / size : 0;
                        ResponseEntity<PageResponse<HechoDTO>> respuesta = fuenteObj.getAllHechosVisiblesPaginated(
                                categoria, fecha_reporte_desde, fecha_reporte_hasta,
                                fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
                                ubicacion, pageExterno, size);
                        
                        if (respuesta != null && respuesta.getBody() != null) {
                            PageResponse<HechoDTO> pageResp = respuesta.getBody();
                            
                            // Normalizar hechos para establecer nombreFuente antes de filtrar
                            Long idFuente = fuenteObj.getId();
                            String nombreFuenteEsperado = fuenteObj.getNombre() != null ? fuenteObj.getNombre().trim() : null;
                            List<HechoDTO> hechosNormalizados = new ArrayList<>();
                            for (HechoDTO h : pageResp.getContent()) {
                                try {
                                    // Normalizar el hecho para establecer nombreFuente
                                    if (h.getId() == null) {
                                        log.warn("[HechosService] Hecho sin id recibido desde fuente {}", idFuente);
                                    } else {
                                        h.setIdOrigen(h.getId());
                                        h.setId(null);
                                    }
                                    h.setFuenteId(idFuente);
                                    // Asegurar que nombreFuente se establece correctamente (normalizado)
                                    if (nombreFuenteEsperado != null) {
                                        h.setNombreFuente(nombreFuenteEsperado);
                                    } else {
                                        // Si nombreFuenteEsperado es null, usar el nombre de la fuente directamente
                                        String nombreFuenteFallback = fuenteObj.getNombre() != null ? fuenteObj.getNombre().trim() : null;
                                        h.setNombreFuente(nombreFuenteFallback);
                                    }
                                    
                                    // Normalizar provincia si es necesario
                                    if (h.getProvincia() == null && h.getLatitud() != null && h.getLongitud() != null) {
                                        Provincia provincia = provinciaService.obtenerProvinciaPorCoordenadas(
                                            h.getLatitud(), h.getLongitud());
                                        if (provincia != null) {
                                            h.setProvincia(provincia.getNombre());
                                        }
                                    }
                                    
                                    hechosNormalizados.add(h);
                                } catch (Exception e) {
                                    log.error("[HechosService] Error normalizando hecho: {}", e.getMessage(), e);
                                    throw e; // Re-lanzar para que se capture en el catch externo
                                }
                            }
                            
                            // Filtrar por fuente si se especificó
                            if (fuente != null && !fuente.trim().isEmpty()) {
                                String fuenteFiltroNormalizada = fuente.trim();
                                hechosNormalizados = hechosNormalizados.stream()
                                    .filter(h -> {
                                        String nombreFuenteHecho = h.getNombreFuente() != null ? h.getNombreFuente().trim() : null;
                                        return nombreFuenteHecho != null && nombreFuenteHecho.equals(fuenteFiltroNormalizada);
                                    })
                                    .toList();
                            }
                            
                            // Actualizar totalElements después del filtro
                            long totalElementsDespuesFiltro = hechosNormalizados.size();
                            pageResp = new PageResponse<>(
                                hechosNormalizados, pageResp.getCurrentPage(), pageResp.getPageSize(),
                                pageResp.isHasNext(), pageResp.isHasPrevious(), totalElementsDespuesFiltro);
                            
                            respuestasPaginadas.add(pageResp);
                        }
                    } catch (Exception e) {
                        log.debug("[HechosService] Fuente {} no soporta paginación o error: {}", 
                            fuenteObj.getNombre(), e.getMessage());
                    }
                }
            }
            
            if (!respuestasPaginadas.isEmpty()) {
                List<HechoDTO> todos = new ArrayList<>();
                long total = 0;
                boolean hasNext = false;
                
                for (PageResponse<HechoDTO> resp : respuestasPaginadas) {
                    todos.addAll(resp.getContent());
                    total += resp.getTotalElements();
                    hasNext = hasNext || resp.isHasNext();
                }
                
                int pageExterno = size > 0 ? offset / size : 0;
                return new PageResponse<>(
                    todos, pageExterno, size,
                    hasNext || todos.size() >= size, offset > 0, total);
            }
            
            return null;
        } catch (Exception e) {
            log.warn("[HechosService] Error al obtener hechos externos paginados: {}", e.getMessage());
            return null;
        }
    }
}