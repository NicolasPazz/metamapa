package ar.utn.ba.ddsi.estadistica.service;

import ar.utn.ba.ddsi.estadistica.dto.output.*;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvExportService {

  private final EstadisticasService estadisticasService;

  public String exportarEstadisticasACSV() {
    log.info("📄 [CsvExportService] Exportando estadísticas a CSV...");

    try {
      EstadisticasCompletasDTO estadisticas = estadisticasService.obtenerEstadisticasCompletas();
      StringWriter stringWriter = new StringWriter();
      CSVWriter csvWriter = new CSVWriter(stringWriter);

      csvWriter.writeNext(new String[]{"ESTADÍSTICAS METAMAPA - GENERADAS EN: " + java.time.LocalDateTime.now()});
      csvWriter.writeNext(new String[]{});

      csvWriter.writeNext(new String[]{"=== DISTRIBUCIÓN GEOGRÁFICA POR COLECCIÓN ==="});
      csvWriter.writeNext(new String[]{"Colección Handle", "Colección Título", "Provincia", "Cantidad Hechos"});

      if (estadisticas.getDistribucionGeograficaColecciones() != null) {
        for (ColeccionProvinciaDTO dto : estadisticas.getDistribucionGeograficaColecciones()) {
          csvWriter.writeNext(new String[]{
              dto.getColeccionHandle(),
              dto.getColeccionTitulo(),
              dto.getProvinciaNombre(),
              String.valueOf(dto.getCantidadHechos())
          });
        }
      }
      csvWriter.writeNext(new String[]{});

      csvWriter.writeNext(new String[]{"=== CATEGORÍA DOMINANTE ==="});
      csvWriter.writeNext(new String[]{"Categoría", "Cantidad Hechos"});

      if (estadisticas.getCategoriaDominante() != null) {
        CategoriaDominanteDTO cd = estadisticas.getCategoriaDominante();
        csvWriter.writeNext(new String[]{
            cd.getCategoriaNombre(),
            String.valueOf(cd.getCantidadHechos())
        });
      }
      csvWriter.writeNext(new String[]{});

      csvWriter.writeNext(new String[]{"=== CONCENTRACIÓN GEOGRÁFICA POR CATEGORÍA ==="});
      csvWriter.writeNext(new String[]{"Categoría", "Provincia", "Cantidad Hechos"});

      if (estadisticas.getConcentracionGeograficaCategorias() != null) {
        for (CategoriaProvinciaDTO dto : estadisticas.getConcentracionGeograficaCategorias()) {
          csvWriter.writeNext(new String[]{
              dto.getCategoriaNombre(),
              dto.getProvinciaNombre(),
              String.valueOf(dto.getCantidadHechos())
          });
        }
      }
      csvWriter.writeNext(new String[]{});

      csvWriter.writeNext(new String[]{"=== DISTRIBUCIÓN TEMPORAL POR CATEGORÍA ==="});
      csvWriter.writeNext(new String[]{"Categoría", "Hora", "Cantidad Hechos"});

      if (estadisticas.getDistribucionTemporalCategorias() != null) {
        for (CategoriaHoraDTO dto : estadisticas.getDistribucionTemporalCategorias()) {
          csvWriter.writeNext(new String[]{
              dto.getCategoriaNombre(),
              String.valueOf(dto.getHora()),
              String.valueOf(dto.getCantidadHechos())
          });
        }
      }
      csvWriter.writeNext(new String[]{});

      csvWriter.writeNext(new String[]{"=== DETECCIÓN DE SPAM ==="});
      csvWriter.writeNext(new String[]{"Total Solicitudes", "Solicitudes Spam", "Porcentaje Spam (%)"});

      if (estadisticas.getEstadisticaSpam() != null) {
        SpamDTO spam = estadisticas.getEstadisticaSpam();
        csvWriter.writeNext(new String[]{
            String.valueOf(spam.getTotalSolicitudes()),
            String.valueOf(spam.getSolicitudesSpam()),
            String.format("%.2f", spam.getPorcentajeSpam())
        });
      }

      csvWriter.close();

      log.info("✅ [CsvExportService] Estadísticas exportadas a CSV exitosamente");
      return stringWriter.toString();

    } catch (Exception e) {
      log.error("❌ [CsvExportService] Error al exportar estadísticas a CSV: {}", e.getMessage(), e);
      return "Error al generar CSV: " + e.getMessage();
    }
  }
}

