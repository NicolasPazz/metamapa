package ar.utn.ba.ddsi.dinamica.service;

import ar.utn.ba.ddsi.dinamica.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.dinamica.domain.hecho.Hecho;
import ar.utn.ba.ddsi.dinamica.domain.hecho.OrigenHecho;
import ar.utn.ba.ddsi.dinamica.domain.repositorie.IHechosRepository;
import ar.utn.ba.ddsi.dinamica.domain.solicitud.SolicitudHecho;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuenteService {
  private final IHechosRepository hechosRepository;

  public List<HechoOutputDTO> getAllHechosVisibles() {
    return List.of();
  }

  @Transactional
  public List<HechoOutputDTO> getAllHechosNuevos() {
    log.info("Obteniendo hechos nuevos no enviados al agregador");

    List<Hecho> hechosNoEnviados = hechosRepository.findByEnviadoFalse();
    log.debug("Se encontraron {} hechos no enviados", hechosNoEnviados.size());

    if (hechosNoEnviados.isEmpty()) {
      return List.of();
    }

    hechosNoEnviados.forEach(hecho -> hecho.setEnviado(true));
    hechosRepository.saveAll(hechosNoEnviados);

    log.info("Se marcaron {} hechos como enviados", hechosNoEnviados.size());

    return hechosNoEnviados.stream()
        .map(Hecho::toOutputDTO)
        .collect(Collectors.toList());
  }

  @Transactional
  public Hecho crearHechoDesdeSolicitud(SolicitudHecho solicitud) {
    log.info("Creando hecho desde solicitud ID: {}", solicitud.getId());

    OrigenHecho origen = solicitud.esAnonimo() ? OrigenHecho.ANONIMO : OrigenHecho.CONTRIBUYENTE;

    Hecho hecho = Hecho.builder()
        .fechaDeCarga(LocalDateTime.now())
        .origen(origen)
        .enviado(false)
        .solicitud(solicitud)
        .build();

    Hecho hechoGuardado = hechosRepository.save(hecho);
    log.info("Hecho creado con ID: {} desde solicitud ID: {} - Origen: {}",
        hechoGuardado.getId(), solicitud.getId(), origen);

    return hechoGuardado;
  }
}
