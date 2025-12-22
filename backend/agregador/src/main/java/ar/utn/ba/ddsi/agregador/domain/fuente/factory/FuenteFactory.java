package ar.utn.ba.ddsi.agregador.domain.fuente.factory;

import ar.utn.ba.ddsi.agregador.domain.fuente.config.FuenteConfig;
import ar.utn.ba.ddsi.agregador.domain.fuente.AFuente;
import ar.utn.ba.ddsi.agregador.domain.fuente.impl.FuenteCron;
import ar.utn.ba.ddsi.agregador.domain.fuente.impl.FuenteTiempoReal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class FuenteFactory {
  
  public AFuente crearFuente(FuenteConfig config) {
    String url = config.getUrl();
    if (url == null || url.trim().isEmpty()) {
      throw new IllegalArgumentException("URL de fuente no puede estar vacía para: " + config.getNombre());
    }
    
    // Validar y normalizar la URL
    url = url.trim();
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
      throw new IllegalArgumentException("URL de fuente debe comenzar con http:// o https://: " + url);
    }
    
    // Extraer componentes de la URL manualmente para normalizar la construcción del WebClient
    String scheme;
    String host;
    int port = -1;
    String path;
    
    try {
      // Extraer scheme
      int schemeEnd = url.indexOf("://");
      if (schemeEnd == -1) {
        throw new IllegalArgumentException("URL de fuente debe tener scheme (http:// o https://): " + url);
      }
      scheme = url.substring(0, schemeEnd);
      
      // Extraer host y port
      int authorityStart = schemeEnd + 3;
      int pathStart = url.indexOf('/', authorityStart);
      if (pathStart == -1) {
        pathStart = url.length();
      }
      
      String authority = url.substring(authorityStart, pathStart);
      int colonIndex = authority.indexOf(':');
      
      if (colonIndex != -1) {
        host = authority.substring(0, colonIndex);
        try {
          port = Integer.parseInt(authority.substring(colonIndex + 1));
        } catch (NumberFormatException e) {
          throw new IllegalArgumentException("Puerto inválido en URL: " + url);
        }
      } else {
        host = authority;
      }
      
      if (host == null || host.isEmpty()) {
        throw new IllegalArgumentException("URL de fuente no tiene host válido: " + url);
      }
      
      // Extraer path
      path = pathStart < url.length() ? url.substring(pathStart) : "";
      // Normalizar path: remover barra final si existe (excepto si es solo "/")
      if (path.endsWith("/") && path.length() > 1) {
        path = path.substring(0, path.length() - 1);
      }
      
      // Construir baseUrl solo con host y puerto (sin path) para evitar problemas con UriBuilder
      // El path se agregará en cada llamada usando UriBuilder
      String normalizedUrl = scheme + "://" + host 
          + (port != -1 ? ":" + port : "");
      
      log.debug("Construyendo WebClient con URL normalizada: {} (host: {}, port: {})", 
          normalizedUrl, host, port != -1 ? port : "default");
      url = normalizedUrl;
    } catch (Exception e) {
      if (e instanceof IllegalArgumentException) {
        throw e;
      }
      throw new IllegalArgumentException("URL de fuente inválida: " + url, e);
    }
    
    // Construir WebClient
    try {
      log.debug("Construyendo WebClient para fuente {} con URL: {} (host: {})", 
          config.getNombre(), url, host);
      
      WebClient webClient = WebClient.builder()
          .baseUrl(url)
          .build();
      
      log.debug("WebClient construido exitosamente para fuente: {}", config.getNombre());
      return createFuenteWithWebClient(config, webClient, url);
    } catch (Exception e) {
      log.error("Error construyendo WebClient para fuente {} con URL {}: {}", 
          config.getNombre(), url, e.getMessage(), e);
      throw new RuntimeException("Error construyendo WebClient para fuente: " + config.getNombre(), e);
    }
  }
  
  private AFuente createFuenteWithWebClient(FuenteConfig config, WebClient webClient, String url) {

    Long idAsignado = config.getId();
    String nombre = config.getNombre() != null ? config.getNombre() : "Fuente " + idAsignado;

    AFuente fuente = switch (config.getTipo()) {
      case CRON -> new FuenteCron(idAsignado, nombre, webClient);
      case TIEMPO_REAL -> new FuenteTiempoReal(idAsignado, nombre, webClient);
    };

    log.info("Fuente {} creada: {} (ID: {}, URL: {})",
        config.getTipo(), nombre, idAsignado, url);

    return fuente;
  }
}
