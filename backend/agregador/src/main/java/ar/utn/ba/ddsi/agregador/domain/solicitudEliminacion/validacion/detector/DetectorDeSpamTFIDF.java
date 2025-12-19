package ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.validacion.detector;

import ar.utn.ba.ddsi.agregador.domain.solicitudEliminacion.validacion.Validacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DetectorDeSpamTFIDF implements DetectorDeSpam, Validacion {

  // Corpus de documentos legítimos (palabras esperadas en solicitudes válidas)
  private final List<String> corpusLegitimo = List.of(
      "hecho evento incidente reporte desastre catástrofe acontecimiento suceso",
      "desaparición accidente emergencia crisis situación problema",
      "coordenadas ubicación calle zona ciudad provincia lugar dirección localización geografía",
      "fecha día mes año hora momento tiempo temporal cronología cuando ocurrió",
      "solicito eliminar ocultar fundamentos justificación razón motivo causa porque",
      "información datos descripción categoría tipo clasificación detalles contenido",
      "verificación evidencia documentación prueba respaldo soporte documento imagen",
      "incorrecto erróneo falso inexacto equivocado error duplicado",
      "privacidad datos personales información sensible protección confidencial",
      "violación normativa legal ley reglamento política términos condiciones"
  );

  // Palabras típicas de spam
  private final Set<String> palabrasSpam = new HashSet<>(Arrays.asList(
      "gratis", "oferta", "descuento", "promoción", "premio", "ganador",
      "click", "aquí", "enlace", "link", "visita", "página",
      "dinero", "efectivo", "ganar", "millones", "urgente", "ahora",
      "compra", "venta", "producto", "servicio", "contacto", "teléfono", "www",
      "whatsapp", "telegram", "email", "correo", "suscríbete", "registrate",
      "xxx", "sexo", "casino", "apuesta", "juego", "lotería",
      "viagra", "medicamento", "pastilla", "tratamiento", "cura", "http", ".com"
  ));


  // Umbral de spam score (0-1, mayor = más estricto)
  private final double umbralSpam = 0.35;

  // Palabras muy cortas o irrelevantes (stopwords)
  private final Set<String> stopwords = new HashSet<>(Arrays.asList(
      "el", "la", "de", "en", "y", "a", "o", "un", "una", "los", "las",
      "del", "al", "por", "para", "con", "sin", "sobre", "entre", "cuando",
      "que", "es", "son", "está", "están", "fue", "ser", "si", "no"
  ));

  @Override
  public boolean esValida(String texto) {
    boolean resultado = texto != null && !esSpam(texto);
    log.info("[DetectorSpamTFIDF] esValida() llamado - Resultado: {}", resultado);
    return resultado;
  }

  @Override
  public boolean esSpam(String texto) {
    log.info("[DetectorSpamTFIDF] esSpam() llamado - Longitud texto: {}",
        texto != null ? texto.length() : 0);

    if (texto == null || texto.isBlank()) {
      log.info("[DetectorSpamTFIDF] Texto null o vacío - NO ES SPAM");
      return false;
    }

    // Normalizar texto
    String textoNorm = normalizarTexto(texto);
    String[] palabras = textoNorm.split("\\s+");

    log.info("[DetectorSpamTFIDF] Texto normalizado - {} palabras", palabras.length);

    if (palabras.length < 10) {
      log.warn("[DetectorSpamTFIDF] Texto muy corto (< 10 palabras) - MARCADO COMO SPAM");
      return true; // Textos muy cortos son sospechosos
    }

    // Calcular score de spam basado en múltiples factores
    double spamScore = 0.0;

    // 1. Verificar palabras de spam explícitas (peso alto)
    double palabrasSpamRatio = calcularRatioPalabrasSpam(palabras);
    spamScore += palabrasSpamRatio * 0.5; // 50% del peso

    // 2. Calcular TF-IDF (relevancia del texto)
    double tfidfScore = calcularTFIDF(palabras);
    spamScore += (1.0 - tfidfScore) * 0.3; // 30% del peso (invertido: bajo TF-IDF = spam)

    // 3. Detectar patrones sospechosos
    double patronesScore = detectarPatrones(texto);
    spamScore += patronesScore * 0.2; // 20% del peso

    // 4. Detectar repeticiones excesivas (spam con copy-paste)
    double repeticionesScore = detectarRepeticionesExcesivas(palabras);
    if (repeticionesScore > 0.7) {
      log.warn("[DetectorSpamTFIDF] Texto con repeticiones excesivas detectado: {}", repeticionesScore);
      return true; // Marcar directamente como spam si hay repeticiones muy altas
    }

    log.debug("[DetectorSpamTFIDF] Spam score: {} (umbral: {})", spamScore, umbralSpam);
    log.debug("[DetectorSpamTFIDF] - Palabras spam: {}", palabrasSpamRatio);
    log.debug("[DetectorSpamTFIDF] - TF-IDF: {}", tfidfScore);
    log.debug("[DetectorSpamTFIDF] - Patrones: {}", patronesScore);
    log.debug("[DetectorSpamTFIDF] - Repeticiones: {}", repeticionesScore);

    return spamScore >= umbralSpam;
  }

  private String normalizarTexto(String texto) {
    return texto.toLowerCase()
        .replaceAll("[áàäâ]", "a")
        .replaceAll("[éèëê]", "e")
        .replaceAll("[íìïî]", "i")
        .replaceAll("[óòöô]", "o")
        .replaceAll("[úùüû]", "u")
        .replaceAll("ñ", "n")
        .replaceAll("[^a-z ]", " ")
        .replaceAll("\\s+", " ")
        .trim();
  }

  private double calcularRatioPalabrasSpam(String[] palabras) {
    long countSpam = Arrays.stream(palabras)
        .filter(palabrasSpam::contains)
        .count();

    return palabras.length > 0 ? (double) countSpam / palabras.length : 0.0;
  }

  private double calcularTFIDF(String[] palabras) {
    // Construir vocabulario del corpus
    Set<String> vocabularioCorpus = new HashSet<>();
    Map<String, Integer> documentFrequency = new HashMap<>();

    for (String doc : corpusLegitimo) {
      Set<String> palabrasDoc = Arrays.stream(doc.split(" "))
          .filter(p -> !stopwords.contains(p))
          .collect(Collectors.toSet());

      vocabularioCorpus.addAll(palabrasDoc);

      for (String palabra : palabrasDoc) {
        documentFrequency.put(palabra, documentFrequency.getOrDefault(palabra, 0) + 1);
      }
    }

    // Calcular TF en el texto analizado
    Map<String, Integer> termFrequency = new HashMap<>();
    for (String palabra : palabras) {
      if (!stopwords.contains(palabra)) {
        termFrequency.put(palabra, termFrequency.getOrDefault(palabra, 0) + 1);
      }
    }

    // Calcular TF-IDF para cada palabra
    double tfidfTotal = 0.0;
    int palabrasRelevantes = 0;
    int totalDocs = corpusLegitimo.size();

    for (Map.Entry<String, Integer> entry : termFrequency.entrySet()) {
      String palabra = entry.getKey();
      int tf = entry.getValue();

      // Si la palabra está en el corpus legítimo, calcular IDF
      if (vocabularioCorpus.contains(palabra)) {
        int df = documentFrequency.getOrDefault(palabra, 1);
        double idf = Math.log((double) totalDocs / df);
        double tfidf = tf * idf;
        tfidfTotal += tfidf;
        palabrasRelevantes++;
      }
    }

    // Normalizar por cantidad de palabras relevantes
    return palabrasRelevantes > 0 ? tfidfTotal / palabrasRelevantes : 0.0;
  }

  private double detectarPatrones(String texto) {
    double score = 0.0;

    // 1. Caracteres repetidos excesivamente (!!!!, ????, etc.)
    if (texto.matches(".*[!?=*#]{3,}.*")) {
      score += 0.3;
    }

    // 2. Mayúsculas excesivas (> 30% del texto)
    long mayusculas = texto.chars().filter(Character::isUpperCase).count();
    long letras = texto.chars().filter(Character::isLetter).count();
    if (letras > 0 && (double) mayusculas / letras > 0.3) {
      score += 0.2;
    }

    // 3. Números excesivos (más del 20% del contenido)
    long numeros = texto.chars().filter(Character::isDigit).count();
    if (texto.length() > 0 && (double) numeros / texto.length() > 0.2) {
      score += 0.2;
    }

    // 4. URLs o emails (sospechoso en una solicitud de eliminación)
    if (texto.matches(".*(http|www\\.|@).*")) {
      score += 0.3;
    }

    return Math.min(score, 1.0); // Limitar a 1.0
  }

  private double detectarRepeticionesExcesivas(String[] palabras) {
    if (palabras.length == 0) {
      return 0.0;
    }

    // Contar palabras únicas (excluyendo stopwords)
    Set<String> palabrasUnicas = new HashSet<>();
    int totalPalabrasRelevantes = 0;

    for (String palabra : palabras) {
      if (!stopwords.contains(palabra) && palabra.length() > 2) {
        palabrasUnicas.add(palabra);
        totalPalabrasRelevantes++;
      }
    }

    // Calcular diversidad léxica
    double diversidadLexica = totalPalabrasRelevantes > 0
        ? (double) palabrasUnicas.size() / totalPalabrasRelevantes
        : 1.0;

    // Invertir: baja diversidad = alta repetición = alto score de spam
    // Diversidad < 0.3 es muy sospechoso (menos del 30% de palabras únicas)
    double repeticionScore = 1.0 - diversidadLexica;

    log.debug("[DetectorSpamTFIDF] Diversidad léxica: {} (palabras únicas: {}, total: {})",
        diversidadLexica, palabrasUnicas.size(), totalPalabrasRelevantes);

    return repeticionScore;
  }

  @Override
  public String mensajeError() {
    return "El texto fue detectado como spam. Asegúrese de proporcionar una justificación legítima y detallada.";
  }
}
