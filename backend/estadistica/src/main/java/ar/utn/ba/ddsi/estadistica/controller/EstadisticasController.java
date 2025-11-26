package ar.utn.ba.ddsi.estadistica.controller;

import ar.utn.ba.ddsi.estadistica.domain.dto.output.*;
import ar.utn.ba.ddsi.estadistica.service.ICsvExport;
import ar.utn.ba.ddsi.estadistica.service.IEstadisticasService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
@Slf4j
public class EstadisticasController {

    private final IEstadisticasService estadisticas;
    private final ICsvExport csvExport;

    /**
     * POST /api/estadisticas/recalcular
     * Endpoint para recalcular todas las estadísticas
     */
    @PostMapping("/recalcular")
    public void recalcular() {
        log.info("Solicitud recibida para recalcular todas las estadísticas");
        try {
            estadisticas.refrescoEstadisticas();
        } catch (Exception e) {
            log.error("Error durante el recálculo de estadísticas: {}", e.getMessage(), e);
        }
    }

    /**
     * GET /api/estadisticas/coleccion/{handle}/provincia-mas-hechos
     * De una colección, ¿en qué provincia se agrupan la mayor cantidad de hechos reportados?
     * Obtiene la provincia con mayor cantidad de hechos reportados para una coleccion especifica
     */
    @GetMapping("/coleccion/{handle}/provincia-mas-hechos")
    public ResponseEntity<ProvinciaPorColeccionDTO> getProvinciaMasHechosPorColeccion(
            @PathVariable String handle) {
        log.info("Solicitando provincia con mas hechos para coleccion: {}", handle);

        try {
            ProvinciaPorColeccionDTO resultado = estadisticas.calcularProvinciaMasHechosPorColeccion(handle);
            log.info("Respuesta exitosa para coleccion {}", handle);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error al obtener estadistica para coleccion {}: {}", handle, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/estadisticas/categoria-mas-hechos
     * ¿Cuál es la categoría con mayor cantidad de hechos reportados?
     * Obtiene la categoria con mayor cantidad de hechos reportados
     */
    @GetMapping("/categoria-mas-hechos")
    public ResponseEntity<CategoriaEstadisticaDTO> getCategoriaMasHechos() {
        log.info("Solicitando categoria con mas hechos reportados");
        
        try {
            CategoriaEstadisticaDTO resultado = estadisticas.calcularCategoriaMasHechos();
            log.info("Respuesta exitosa para categoria con mas hechos");
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error al obtener categoria con mas hechos: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/estadisticas/categoria/{categoria}/provincia-mas-hechos
     * ¿En qué provincia se presenta la mayor cantidad de hechos de una cierta categoría?
     * Obtiene la provincia con mayor cantidad de hechos para una categoria especifica
     */
    @GetMapping("/categoria/{categoria}/provincia-mas-hechos")
    public ResponseEntity<ProvinciaPorCategoriaDTO> getProvinciaMasHechosPorCategoria(
            @PathVariable String categoria) {
        log.info("Solicitando provincia con mas hechos para categoria: {}", categoria);

        try {
            ProvinciaPorCategoriaDTO resultado = estadisticas.calcularProvinciaMasHechosPorCategoria(categoria);
            log.info("Respuesta exitosa para categoria {}", categoria);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error al obtener estadistica para categoria {}: {}", categoria, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/estadisticas/categoria/{categoria}/hora-mas-hechos
     *  ¿A qué hora del día ocurren la mayor cantidad de hechos de una cierta categoría?
     * Obtiene la hora del dia con mayor cantidad de hechos para una categoria especifica
     */
    @GetMapping("/categoria/{categoria}/hora-mas-hechos")
    public ResponseEntity<HoraEstadisticaDTO> getHoraMasHechosPorCategoria(
            @PathVariable String categoria) {
        log.info("Solicitando hora con mas hechos para categoria: {}", categoria);
        
        try {
            HoraEstadisticaDTO resultado = estadisticas.calcularHoraMasHechosPorCategoria(categoria);
            log.info("Respuesta exitosa para hora de categoria {}", categoria);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error al obtener estadistica de hora para categoria {}: {}", categoria, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/estadisticas/solicitudes-spam
     * ¿Cuántas solicitudes de eliminación son spam?
     * Obtiene estadisticas sobre solicitudes de eliminacion que son spam
     */
    @GetMapping("/solicitudes-spam")
    public ResponseEntity<SpamEstadisticaDTO> getSolicitudesSpam() {
        log.info("Solicitando estadisticas de spam en solicitudes");
        
        try {
            SpamEstadisticaDTO resultado = estadisticas.calcularSolicitudesSpam();
            log.info("Respuesta exitosa para estadisticas de spam");
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Error al obtener estadisticas de spam: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    //------------------------------------------------ CSV ---------------------------------------------------------
    /**
     * GET /api/estadisticas/export/csv
     * Exporta todas las estadisticas a un archivo CSV
     */
    @GetMapping("/export/csv")
    public ResponseEntity<Resource> exportarEstadisticasCSV() {
        log.info("Solicitando exportacion completa de estadisticas a CSV");
        
        try {
            Resource resource = csvExport.exportarEstadisticasCompletas();
            String filename = csvExport.generarNombreArchivo("completo");
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
                    
        } catch (IOException e) {
            log.error("Error al exportar estadisticas a CSV: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/estadisticas/export/csv/{tipo}
     * Exporta estadisticas especificas a un archivo CSV
     */
    @GetMapping("/export/csv/{tipo}")
    public ResponseEntity<Resource> exportarEstadisticasPorTipo(@PathVariable String tipo) {
        log.info("Solicitando exportacion de estadisticas de tipo: {}", tipo);
        
        try {
            Resource resource = csvExport.exportarEstadisticasPorTipo(tipo);
            String filename = csvExport.generarNombreArchivo(tipo);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
                    
        } catch (IOException e) {
            log.error("Error al exportar estadisticas de tipo {} a CSV: {}", tipo, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        } catch (IllegalArgumentException e) {
            log.error("Tipo de estadistica no valido: {}", tipo);
            return ResponseEntity.badRequest().build();
        }
    }
}
