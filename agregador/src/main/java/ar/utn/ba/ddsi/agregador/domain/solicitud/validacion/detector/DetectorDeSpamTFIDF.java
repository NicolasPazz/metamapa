package ar.utn.ba.ddsi.agregador.domain.solicitud.validacion.detector;

import ar.utn.ba.ddsi.agregador.domain.solicitud.validacion.Validacion;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class DetectorDeSpamTFIDF implements DetectorDeSpam, Validacion {
  // Algoritmo TF-IDF
  // Corpus simple de documentos validos
  private final List<String> corpus = List.of(
      "evento mapa calle fecha",
      "hecho reclamo zona marcha",
      "coordenadas lugar fecha reporte"
  );

  private final double umbral = 1.0;

  @Override
  public boolean esValida(String texto) {
    return texto != null && !esSpam(texto);
  }

  @Override
  public boolean esSpam(String texto) {
    String textoNorm = texto.toLowerCase().replaceAll("[^a-záéíóúñ ]", "").trim();
    String[] palabras = textoNorm.split("\\s+");

    Set<String> vocabulario = new HashSet<>();
    for (String doc : corpus) {
      vocabulario.addAll(Arrays.asList(doc.split(" ")));
    }

    // Calculo TF
    Map<String, Integer> freq = new HashMap<>();
    for (String p : palabras) freq.put(p, freq.getOrDefault(p, 0) + 1);

    double tfidfTotal = 0;

    for (String palabra : freq.keySet()) {
      // TF --> frecuencia de palabra / total palabras
      double tf = freq.get(palabra) / (double) palabras.length;

      // IDF --> log(total docs / docs con palabra + 1)
      int docsConPalabra = 0;
      for (String doc : corpus) {
        if (doc.contains(palabra)) docsConPalabra++;
      }
      double idf = Math.log((double) corpus.size() / (1 + docsConPalabra));

      tfidfTotal += tf * idf;
    }

    return tfidfTotal > umbral;
  }
}
