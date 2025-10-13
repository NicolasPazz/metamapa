package ar.utn.ba.ddsi.estadistica.service.impl;

import ar.utn.ba.ddsi.estadistica.domain.dto.output.*;
import ar.utn.ba.ddsi.estadistica.service.ICsvExport;
import ar.utn.ba.ddsi.estadistica.service.IEstadisticasService;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvExport implements ICsvExport {

    private final IEstadisticasService estadisticas;

    @Value("${estadisticas.export.filename-prefix:estadisticas_}")
    private String filenamePrefix;

    @Override
    public Resource exportarEstadisticasCompletas() throws IOException {
        log.info("Iniciando exportacion completa de estadisticas a CSV");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        try {
            // Header del archivo
            csvWriter.writeNext(new String[]{"Tipo de Estadistica", "Valor", "Cantidad", "Detalles", "Fecha de Exportacion"});
            csvWriter.writeNext(new String[]{"", "", "", "", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)});
            csvWriter.writeNext(new String[]{}); // Linea vacia

            // Exportar categoria con mas hechos
            try {
                CategoriaEstadisticaDTO categoria = estadisticas.calcularCategoriaMasHechos();
                csvWriter.writeNext(new String[]{"Categoria con mas hechos", categoria.getCategoria(), 
                        categoria.getCantidadHechos().toString(), "", ""});
            } catch (Exception e) {
                log.error("Error al escribir categoria en CSV: {}", e.getMessage());
            }

            // Exportar estadisticas de spam
            try {
                SpamEstadisticaDTO spam = estadisticas.calcularSolicitudesSpam();
                csvWriter.writeNext(new String[]{"Total solicitudes", "", spam.getTotalSolicitudes().toString(), "", ""});
                csvWriter.writeNext(new String[]{"Solicitudes spam", "", spam.getSolicitudesSpam().toString(), 
                        String.format("%.2f%%", spam.getPorcentajeSpam()), ""});
                csvWriter.writeNext(new String[]{"Solicitudes validas", "", spam.getSolicitudesValidas().toString(), "", ""});
            } catch (Exception e) {
                log.error("Error al escribir estadisticas de spam en CSV: {}", e.getMessage());
            }


            csvWriter.close();
            String csvContent = stringWriter.toString();
            outputStream.write(csvContent.getBytes());

            log.info("Exportacion CSV completada exitosamente");

            return new ByteArrayResource(outputStream.toByteArray());

        } catch (Exception e) {
            log.error("Error durante la exportacion CSV: {}", e.getMessage(), e);
            throw new IOException("Error al generar archivo CSV", e);
        }
    }

    @Override
    public Resource exportarEstadisticasPorTipo(String tipoEstadistica) throws IOException {
        log.info("Exportando estadisticas de tipo: {}", tipoEstadistica);

        StringWriter stringWriter = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(stringWriter);

        try {
            switch (tipoEstadistica.toLowerCase()) {
                case "categorias":
                    exportarCategorias(csvWriter);
                    break;
                case "spam":
                    exportarSpam(csvWriter);
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de estadistica no valido: " + tipoEstadistica);
            }

            csvWriter.close();
            String csvContent = stringWriter.toString();

            log.info("Exportacion de {} completada", tipoEstadistica);
            return new ByteArrayResource(csvContent.getBytes());

        } catch (Exception e) {
            log.error("Error al exportar estadisticas de tipo {}: {}", tipoEstadistica, e.getMessage(), e);
            throw new IOException("Error al generar archivo CSV para " + tipoEstadistica, e);
        }
    }

    private void exportarCategorias(CSVWriter csvWriter) throws IOException {
        csvWriter.writeNext(new String[]{"Categoria", "Cantidad de Hechos"});
        
        try {
            CategoriaEstadisticaDTO categoria = estadisticas.calcularCategoriaMasHechos();
            csvWriter.writeNext(new String[]{categoria.getCategoria(), categoria.getCantidadHechos().toString()});
        } catch (Exception e) {
            log.error("Error al escribir categoria: {}", e.getMessage());
        }
    }

    private void exportarSpam(CSVWriter csvWriter) throws IOException {
        csvWriter.writeNext(new String[]{"Metrica", "Valor", "Porcentaje"});
        
        try {
            SpamEstadisticaDTO spam = estadisticas.calcularSolicitudesSpam();
            csvWriter.writeNext(new String[]{"Total Solicitudes", spam.getTotalSolicitudes().toString(), "100%"});
            csvWriter.writeNext(new String[]{"Solicitudes Spam", spam.getSolicitudesSpam().toString(), 
                    String.format("%.2f%%", spam.getPorcentajeSpam())});
            csvWriter.writeNext(new String[]{"Solicitudes Validas", spam.getSolicitudesValidas().toString(), 
                    String.format("%.2f%%", 100 - spam.getPorcentajeSpam())});
        } catch (Exception e) {
            log.error("Error al escribir estadisticas de spam: {}", e.getMessage());
        }
    }


    @Override
    public String generarNombreArchivo(String tipo) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return filenamePrefix + tipo + "_" + timestamp + ".csv";
    }
}
