package ar.utn.ba.ddsi.agregador.domain.fuente.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "fuentes")
public class FuentesProperties {
  private List<FuenteConfig> config;
}
