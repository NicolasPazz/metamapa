package ar.utn.ba.ddsi.frontend.service;

import ar.utn.ba.ddsi.frontend.models.dtos.EstadisticasCompletasDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EstadisticasService {
    private final WebClient webClient;

    public EstadisticasService(
            WebClient.Builder builder,
            @Value("${app.api.estadistica.base-url:http://localhost:8085/api/estadisticas}") String estadisticaBaseUrl
    ) {
        // #region agent log
        try {
            java.io.FileWriter fw = new java.io.FileWriter("d:\\Materias\\DDS\\2025-tpa-mi-no-grupo-10\\.cursor\\debug.log", true);
            fw.write("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A\",\"location\":\"EstadisticasService.java:14\",\"message\":\"EstadisticasService constructor - estadisticaBaseUrl configurada\",\"data\":{\"estadisticaBaseUrl\":\"" + estadisticaBaseUrl.replace("\"", "\\\"") + "\"},\"timestamp\":" + System.currentTimeMillis() + "}\n");
            fw.close();
        } catch (Exception e) {}
        // #endregion
        this.webClient = builder.baseUrl(estadisticaBaseUrl).build();
    }

    public EstadisticasCompletasDTO obtenerEstadisticas() {
        // #region agent log
        try {
            java.io.FileWriter fw = new java.io.FileWriter("d:\\Materias\\DDS\\2025-tpa-mi-no-grupo-10\\.cursor\\debug.log", true);
            fw.write("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A\",\"location\":\"EstadisticasService.java:19\",\"message\":\"obtenerEstadisticas - antes de llamar\",\"data\":{},\"timestamp\":" + System.currentTimeMillis() + "}\n");
            fw.close();
        } catch (Exception e) {}
        // #endregion
        try {
            EstadisticasCompletasDTO result = webClient.get()
                    .uri("")
                    .retrieve()
                    .bodyToMono(EstadisticasCompletasDTO.class)
                    .block();
            // #region agent log
            try {
                java.io.FileWriter fw = new java.io.FileWriter("d:\\Materias\\DDS\\2025-tpa-mi-no-grupo-10\\.cursor\\debug.log", true);
                fw.write("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A\",\"location\":\"EstadisticasService.java:26\",\"message\":\"obtenerEstadisticas - exito\",\"data\":{},\"timestamp\":" + System.currentTimeMillis() + "}\n");
                fw.close();
            } catch (Exception e) {}
            // #endregion
            return result;
        } catch (Exception e) {
            // #region agent log
            try {
                java.io.FileWriter fw = new java.io.FileWriter("d:\\Materias\\DDS\\2025-tpa-mi-no-grupo-10\\.cursor\\debug.log", true);
                String errorMsg = e.getMessage() != null ? e.getMessage().replace("\"", "\\\"").replace("\n", "\\n") : "null";
                fw.write("{\"sessionId\":\"debug-session\",\"runId\":\"run1\",\"hypothesisId\":\"A\",\"location\":\"EstadisticasService.java:32\",\"message\":\"obtenerEstadisticas - error\",\"data\":{\"error\":\"" + e.getClass().getName() + "\",\"message\":\"" + errorMsg + "\"},\"timestamp\":" + System.currentTimeMillis() + "}\n");
                fw.close();
            } catch (Exception ex) {}
            // #endregion
            throw e;
        }
    }
}
