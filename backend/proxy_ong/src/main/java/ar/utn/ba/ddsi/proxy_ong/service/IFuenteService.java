package ar.utn.ba.ddsi.proxy_ong.service;

import ar.utn.ba.ddsi.proxy_ong.domain.dto.output.HechoOutputDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface IFuenteService {
  /**
   * Devuelve todos los hechos que todavia no se habian enviado.
   * @return lista reactiva (Mono) con hechos nuevos
   */
  ResponseEntity<List<HechoOutputDTO>> getAllHechosNuevos();
}
