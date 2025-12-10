package ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.validacion.detector;

import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.validacion.Validacion;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class DetectorDeSpamTFIDF implements DetectorDeSpam, Validacion {
  private final List<String> corpus = List.of(
      "hecho evento incidente reporte",
      "desaparición accidente emergencia",
      "coordenadas calle zona ciudad provincia lugar",
      "fecha día mes año hora",
      "solicito eliminar ocultar fundamentos justificación",
      "información descripción categoría tipo",
      "reporte verificación evidencia documentación"
  );

  private final double umbral = 0.5;

  @Override
  public boolean esValida(String texto) {
    return texto != null && !esSpam(texto);
  }

  @Override
  public boolean esSpam(String texto) {
    if (texto == null || texto.isBlank()) return false;

    String textoNorm = texto.toLowerCase().replaceAll("[^a-záéíóúñ ]", "").trim();
    String[] palabras = textoNorm.split("\\s+");

    Set<String> vocabulario = new HashSet<>();
    for (String doc : corpus) {
      vocabulario.addAll(Arrays.asList(doc.split(" ")));
    }

    // Calculo TF-IDF simplificado: penaliza palabras fuera del vocabulario
    double tfidfTotal = 0;
    for (String palabra : palabras) {
      if (!vocabulario.contains(palabra)) {
        tfidfTotal += 0.1; // peso por palabra desconocida
      }
    }

    return tfidfTotal > umbral;
  }

  @Override
  public String mensajeError() {
    return "El texto fue detectado como spam";
  }
}
