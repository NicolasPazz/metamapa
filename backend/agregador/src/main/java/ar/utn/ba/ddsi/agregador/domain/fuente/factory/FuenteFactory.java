package ar.utn.ba.ddsi.agregador.domain.fuente.factory;

import ar.utn.ba.ddsi.agregador.domain.fuente.config.FuenteConfig;
import ar.utn.ba.ddsi.agregador.domain.fuente.AFuente;
import ar.utn.ba.ddsi.agregador.domain.fuente.impl.FuenteCron;
import ar.utn.ba.ddsi.agregador.domain.fuente.impl.FuenteTiempoReal;
import io.netty.channel.ChannelOption;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

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
      
      if (host.isEmpty()) {
        throw new IllegalArgumentException("URL de fuente no tiene host válido: " + url);
      }
      
      // Extraer path (no se usa en la construcción de baseUrl, solo para logging)
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
    
    // Construir WebClient con timeout configurado
    try {
      log.debug("Construyendo WebClient para fuente {} con URL: {} (host: {})", 
          config.getNombre(), url, host);
      
      int responseTimeoutSeconds = 120;
      HttpClient httpClient = HttpClient.create()
          .responseTimeout(Duration.ofSeconds(responseTimeoutSeconds))  // Timeout de respuesta: 60 segundos
          .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);  // Timeout de conexión: 3 segundos
      
      final String urlFuente = url;
      String internalToken = System.getenv().getOrDefault("INTERNAL_API_TOKEN", "");
      boolean esServicioInterno = java.util.stream.Stream.of("ESTATICA_URL", "DINAMICA_URL", "PROXY_ONG_URL", "PROXY_METAMAPA_URL")
          .map(System::getenv)
          .anyMatch(interna -> mismoOrigen(interna, urlFuente));
      WebClient webClient = WebClient.builder()
          .baseUrl(url)
          .clientConnector(new ReactorClientHttpConnector(httpClient))
          // El token interno solo se envia a los servicios propios, nunca a fuentes externas
          .defaultHeaders(h -> { if (esServicioInterno && !internalToken.isBlank()) h.set("X-Internal-Token", internalToken); })
          .build();
      
      log.debug("WebClient construido exitosamente para fuente: {} (con timeout de {}s)", config.getNombre(), responseTimeoutSeconds);
      return createFuenteWithWebClient(config, webClient, url);
    } catch (Exception e) {
      log.error("Error construyendo WebClient para fuente {} con URL {}: {}", 
          config.getNombre(), url, e.getMessage(), e);
      throw new RuntimeException("Error construyendo WebClient para fuente: " + config.getNombre(), e);
    }
  }
  
  /** true si ambas URLs tienen el mismo esquema, host y puerto. */
  private static boolean mismoOrigen(String a, String b) {
    if (a == null || b == null) {
      return false;
    }
    try {
      java.net.URI ua = java.net.URI.create(a.trim());
      java.net.URI ub = java.net.URI.create(b.trim());
      return ua.getHost() != null
          && ua.getHost().equalsIgnoreCase(ub.getHost())
          && String.valueOf(ua.getScheme()).equalsIgnoreCase(String.valueOf(ub.getScheme()))
          && ua.getPort() == ub.getPort();
    } catch (IllegalArgumentException e) {
      return false;
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
