package ar.utn.ba.ddsi.proxy_ong.service;

import ar.utn.ba.ddsi.proxy_ong.adapter.IApiExternaAdapter;
import ar.utn.ba.ddsi.proxy_ong.domain.dto.output.HechoOutputDTO;
import ar.utn.ba.ddsi.proxy_ong.domain.hecho.Hecho;
import ar.utn.ba.ddsi.proxy_ong.domain.repositorie.IHechosRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

@Slf4j
@Service
public class FuenteService {

  private final IApiExternaAdapter apiAdapter;
  private final IHechosRepository hechosRepository;

  public FuenteService(
      IApiExternaAdapter apiAdapter,
      IHechosRepository hechosRepository
  ) {
    this.apiAdapter = apiAdapter;
    this.hechosRepository = hechosRepository;
  }

  public ResponseEntity<List<HechoOutputDTO>> getAllHechosNuevos() {
    long startTime = System.currentTimeMillis();
    try {
      long timeBeforeAdapter = System.currentTimeMillis();
      List<Hecho> todosLosHechos = apiAdapter.obtenerTodosLosHechos();
      long adapterTime = System.currentTimeMillis() - timeBeforeAdapter;
      long timeBeforeProcess = System.currentTimeMillis();
      List<HechoOutputDTO> hechosNuevos = procesarHechosNuevos(todosLosHechos);
      long processTime = System.currentTimeMillis() - timeBeforeProcess;
      long totalTime = System.currentTimeMillis() - startTime;

      return ResponseEntity.of(Optional.of(hechosNuevos));

    } catch (Exception e) {
      long totalTime = System.currentTimeMillis() - startTime;
      log.error("Error al obtener hechos nuevos desde el proxy ONG", e);
      return ResponseEntity.internalServerError().build();
    }
  }

  private List<HechoOutputDTO> procesarHechosNuevos(List<Hecho> hechos) {
    long startTime = System.currentTimeMillis();
    
    if (hechos == null || hechos.isEmpty()) {
      return new ArrayList<>();
    }
    
    // Optimización: Obtener todos los IDs existentes en una sola consulta (evita N+1)
    long timeBeforeCheck = System.currentTimeMillis();
    List<Long> idsHechos = hechos.stream().map(Hecho::getId).collect(Collectors.toList());
    List<Hecho> hechosExistentes = hechosRepository.findAllById(idsHechos);
    Set<Long> idsExistentes = hechosExistentes.stream().map(Hecho::getId).collect(Collectors.toSet());
    long checkTime = System.currentTimeMillis() - timeBeforeCheck;
    
    // Filtrar hechos nuevos y preparar para guardar en batch
    List<Hecho> hechosNuevosParaGuardar = new ArrayList<>();
    List<HechoOutputDTO> nuevos = new ArrayList<>();
    
    for (Hecho hecho : hechos) {
      if (!idsExistentes.contains(hecho.getId())) {
        hechosNuevosParaGuardar.add(hecho);
        nuevos.add(convertirAOutputDTO(hecho));
      }
    }
    
    // Guardar todos los hechos nuevos en batch (una sola operación)
    long timeBeforeSave = System.currentTimeMillis();
    if (!hechosNuevosParaGuardar.isEmpty()) {
      hechosRepository.saveAll(hechosNuevosParaGuardar);
    }
    long saveTime = System.currentTimeMillis() - timeBeforeSave;
    
    long totalTime = System.currentTimeMillis() - startTime;

    log.info("Se procesaron {} hechos nuevos de un total de {} (optimizado con batch)", nuevos.size(), hechos.size());
    return nuevos;
  }

  private HechoOutputDTO convertirAOutputDTO(Hecho hecho) {
    return new HechoOutputDTO(
        hecho.getId(),
        "ONG",
        hecho.getTitulo(),
        hecho.getDescripcion(),
        hecho.getCategoria(),
        hecho.getLatitud(),
        hecho.getLongitud(),
        hecho.getFechaDeAcontecimiento(),
        hecho.getFechaDeCarga()
    );
  }
}
