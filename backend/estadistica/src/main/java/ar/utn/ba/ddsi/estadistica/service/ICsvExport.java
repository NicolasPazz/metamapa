package ar.utn.ba.ddsi.estadistica.service;

import org.springframework.core.io.Resource;

import java.io.IOException;

public interface ICsvExport {
    // Exporta todas las estadisticas
    Resource exportarEstadisticasCompletas() throws IOException;
    // Exporta las estadisticas por tipo de estadistica
    Resource exportarEstadisticasPorTipo(String tipoEstadistica) throws IOException;
    // Genera el nombre del archivo
    String generarNombreArchivo(String tipo);
}
