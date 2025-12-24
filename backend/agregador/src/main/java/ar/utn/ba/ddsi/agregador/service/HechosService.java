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

        List<HechoDTO> resultado = new ArrayList<>();
        int start = page * size;
        int needed = size;
        long totalLocales;
        long totalExternos = 0;

        ResponseEntity<List<HechoDTO>> localesResp = getAllLocal(
                categoria, fecha_reporte_desde, fecha_reporte_hasta,
                fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion, fuente);

        List<HechoDTO> hechosLocales = Optional.ofNullable(localesResp.getBody()).orElse(new ArrayList<>());
        totalLocales = hechosLocales.size();

        if (start < hechosLocales.size()) {
            int endLocal = Math.min(start + size, hechosLocales.size());
            resultado.addAll(hechosLocales.subList(start, endLocal));
            needed = size - resultado.size();
        } else {
            start = start - (int)totalLocales;
        }

        if (needed > 0 || start >= 0) {
            try {
                ResponseEntity<List<HechoDTO>> externosResp = getAllExternal(
                        categoria, fecha_reporte_desde, fecha_reporte_hasta,
                        fecha_acontecimiento_desde, fecha_acontecimiento_hasta, ubicacion, fuente);
                List<HechoDTO> hechosExternos = Optional.ofNullable(externosResp.getBody()).orElse(new ArrayList<>());
                totalExternos = hechosExternos.size();
                
                if (needed > 0 && !hechosExternos.isEmpty() && start >= 0 && start < hechosExternos.size()) {
                    int endExterno = Math.min(start + needed, hechosExternos.size());
                    resultado.addAll(hechosExternos.subList(start, endExterno));
                }
            } catch (Exception e) {
                log.warn("[HechosService] Error al obtener hechos externos: {}", e.getMessage());
            }
        }

        long totalDisponibles = totalLocales + totalExternos;
        boolean hasNext = ((long)(page + 1)) * size < totalDisponibles;


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