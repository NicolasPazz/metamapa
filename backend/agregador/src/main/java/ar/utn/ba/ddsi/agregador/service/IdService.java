package ar.utn.ba.ddsi.agregador.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class IdService {

  private final Long maxHechosPorFuente;
  private final Long maxFuentes;

  public IdService(
      @Value("${idgenerator.maxHechosPorFuente}") Long maxHechosPorFuente,
      @Value("${idgenerator.maxFuentes}") Long maxFuentes
  ) {
    this.maxHechosPorFuente = maxHechosPorFuente;
    this.maxFuentes = maxFuentes;
  }

  public Long getId(Long idFuente, Long idOrigen) {
    if (idFuente >= maxFuentes || idOrigen >= maxHechosPorFuente) {
      throw new IllegalArgumentException("idFuente o idOrigen exceden el máximo permitido");
    }
    return idFuente * maxHechosPorFuente + idOrigen;
  }

  public Long getIdFuente(Long idUnico) {
    return idUnico / maxHechosPorFuente;
  }

  public Long getIdOrigen(Long idUnico) {
    return idUnico % maxHechosPorFuente;
  }
}
