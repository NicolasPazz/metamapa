package ar.utn.ba.ddsi.agregador.service;

import ar.utn.ba.ddsi.agregador.domain.hecho.Etiqueta;
import ar.utn.ba.ddsi.agregador.dto.HechoDTO;
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
    public ResponseEntity<List<HechoDTO>> getAll(
        String categoria,
        LocalDate fecha_reporte_desde, LocalDate fecha_reporte_hasta,
        LocalDateTime fecha_acontecimiento_desde, LocalDateTime fecha_acontecimiento_hasta,
        String ubicacion) {

        log.info("[HechosService] Obteniendo todos los hechos con filtros: categoria={}, ubicacion={}",
            categoria, ubicacion);

        // Obtener hechos locales desde la BD (solo hechos VISIBLES)
        List<Hecho> hechosLocales = hechosRepository.findByFiltersConUbicacion(
            categoria,
            fecha_reporte_desde, fecha_reporte_hasta,
            fecha_acontecimiento_desde, fecha_acontecimiento_hasta,
            ubicacion);

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
                ar.utn.ba.ddsi.agregador.domain.ubicacion.Provincia provincia =
                    provinciaService.obtenerProvinciaPorCoordenadas(
                        hechoNuevo.getLatitud(), hechoNuevo.getLongitud());
                hechoNuevo.setProvincia(provincia);
                log.debug("[HechosService] Provincia asignada: {}", provincia.getNombre());
            } else if (hechoInputDTO.getProvincia() != null) {
                ar.utn.ba.ddsi.agregador.domain.ubicacion.Provincia provincia =
                    provinciaService.obtenerOCrearProvincia(hechoInputDTO.getProvincia());
                hechoNuevo.setProvincia(provincia);
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

    /**
     * Obtiene la lista de provincias que tienen al menos un hecho registrado.
     * @return ResponseEntity con lista de nombres de provincias
     */
    public ResponseEntity<List<String>> getProvinciasConHechos() {
        log.info("[HechosService] Obteniendo provincias con hechos");
        return provinciaService.getProvinciasConHechos();
    }
}