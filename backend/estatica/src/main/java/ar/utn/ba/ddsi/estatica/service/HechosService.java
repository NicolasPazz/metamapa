package ar.utn.ba.ddsi.estatica.service;

import ar.utn.ba.ddsi.estatica.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.estatica.domain.hecho.Hecho;
import ar.utn.ba.ddsi.estatica.repositorie.IHechosRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HechosService {

  private final IHechosRepository hechosRepository;

  @Transactional
  public List<HechoOutputDTO> obtenerHechosNuevos() {
    List<Hecho> hechosNoEnviados = hechosRepository.findByEnviadoFalse();

    if (hechosNoEnviados.isEmpty()) {
      log.info("No hay hechos nuevos para enviar");
      return Collections.emptyList();
    }

    log.info("Enviando {} hechos nuevos", hechosNoEnviados.size());

    marcarHechosComoEnviados(hechosNoEnviados);
    return convertirADTOs(hechosNoEnviados);
  }

  private void marcarHechosComoEnviados(List<Hecho> hechos) {
    hechos.forEach(hecho -> hecho.setEnviado(true));
    hechosRepository.saveAll(hechos);
  }

  private List<HechoOutputDTO> convertirADTOs(List<Hecho> hechos) {
    return hechos.stream()
        .map(Hecho::toOutputDTO)
        .collect(Collectors.toList());
  }
}

