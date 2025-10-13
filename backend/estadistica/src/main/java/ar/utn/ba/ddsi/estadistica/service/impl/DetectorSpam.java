package ar.utn.ba.ddsi.estadistica.service.impl;

import ar.utn.ba.ddsi.estadistica.service.IDetectorSpam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class DetectorSpam implements IDetectorSpam {

    // Algoritmo TF-IDF (reutilizado del agregador)
    // Corpus simple de documentos validos
    private final List<String> corpus = List.of(
            "evento mapa calle fecha",
            "hecho reclamo zona marcha",
            "coordenadas lugar fecha reporte"
    );

    private final double umbral = 1.0;

    @Override
    public boolean esSpam(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return true;
        }

        String textoNorm = texto.toLowerCase().replaceAll("[^a-z ]", "").trim();
        String[] palabras = textoNorm.split("\\s+");

        if (palabras.length == 0) {
            return true;
        }

        Set<String> vocabulario = new HashSet<>();
        for (String doc : corpus) {
            vocabulario.addAll(Arrays.asList(doc.split(" ")));
        }

        // Calculo TF
        Map<String, Integer> freq = new HashMap<>();
        for (String p : palabras) {
            freq.put(p, freq.getOrDefault(p, 0) + 1);
        }

        double tfidfTotal = 0;

        for (String palabra : freq.keySet()) {
            // TF --> frecuencia de palabra / total palabras
            double tf = freq.get(palabra) / (double) palabras.length;

            // IDF --> log(total docs / docs con palabra + 1)
            int docsConPalabra = 0;
            for (String doc : corpus) {
                if (doc.contains(palabra)) {
                    docsConPalabra++;
                }
            }
            double idf = Math.log((double) corpus.size() / (1 + docsConPalabra));

            tfidfTotal += tf * idf;
        }

        boolean esSpam = tfidfTotal > umbral;
        log.debug("Texto: '{}' - TF-IDF: {} - Es spam: {}", texto.substring(0, Math.min(50, texto.length())), tfidfTotal, esSpam);
        
        return esSpam;
    }

    public boolean esValida(String texto) {
        return texto != null && !esSpam(texto);
    }
}
